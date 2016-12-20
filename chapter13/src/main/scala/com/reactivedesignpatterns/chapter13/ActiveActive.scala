/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter13

import akka.actor._
import play.api.libs.json.JsValue
import scala.collection.immutable.TreeMap
import scala.concurrent.duration._
import scala.annotation.tailrec

object ActiveActive {
  import ReplicationProtocol._
  import Persistence._

  private case class SeqCommand(seq: Int, cmd: Command, replyTo: ActorRef)
  private case class SeqResult(seq: Int, res: Result, replica: ActorRef, replyTo: ActorRef)

  private case class SendInitialData(toReplica: ActorRef)
  private case class InitialData(map: Map[String, JsValue])

  class Replica extends Actor with Stash {
    var map = Map.empty[String, JsValue]

    def receive = {
      case InitialData(m) =>
        map = m
        context.become(initialized)
        unstashAll()
      case _ => stash()
    }

    def initialized: Receive = {
      case SeqCommand(seq, cmd, replyTo) =>
        // tracking of sequence numbers and resends is elided here
        cmd match {
          case Put(key, value, r) =>
            map += key -> value
            replyTo ! SeqResult(seq, PutConfirmed(key, value), self, r)
          case Get(key, r) =>
            replyTo ! SeqResult(seq, GetResult(key, map get key), self, r)
        }
      case SendInitialData(toReplica) => toReplica ! InitialData(map)
    }
  }

  object Replica {
    val props = Props(new Replica)
  }

  private sealed trait ReplyState {
    def deadline: Deadline
    def missing: Set[ActorRef]
    def add(res: SeqResult): ReplyState
    def isFinished: Boolean = missing.isEmpty
  }
  private case class Unknown(deadline: Deadline, replies: Set[SeqResult], missing: Set[ActorRef], quorum: Int) extends ReplyState {
    override def add(res: SeqResult): ReplyState = {
      val nextReplies = replies + res
      val nextMissing = missing - res.replica
      if (nextReplies.size >= quorum) {
        val answer = replies.toSeq.groupBy(_.res).collectFirst { case (k, s) if s.size >= quorum => s.head }
        if (answer.isDefined) {
          val right = answer.get
          val wrong = replies.collect { case SeqResult(_, res, replica, _) if res != right => replica }
          Known(deadline, right, wrong, nextMissing)
        } else if (nextMissing.isEmpty) Known.fromUnknown(deadline, nextReplies)
        else Unknown(deadline, nextReplies, nextMissing, quorum)
      } else Unknown(deadline, nextReplies, nextMissing, quorum)
    }
  }
  private case class Known(deadline: Deadline, reply: SeqResult, wrong: Set[ActorRef], missing: Set[ActorRef]) extends ReplyState {
    override def add(res: SeqResult): ReplyState = {
      val nextWrong = if (res.res == reply.res) wrong else wrong + res.replica
      Known(deadline, reply, nextWrong, missing - res.replica)
    }
  }
  private object Known {
    def fromUnknown(deadline: Deadline, replies: Set[SeqResult]): Known = {
      // did not reach consensus on this one, pick simple majority
      val counts = replies.groupBy(_.res)
      val biggest = counts.iterator.map(_._2.size).max
      val winners = counts.collectFirst {
        case (res, win) if win.size == biggest => win
      }.get
      val losers = (replies -- winners).map(_.replica)
      Known(deadline, winners.head, losers, Set.empty)
    }
  }

  class Coordinator(N: Int) extends Actor {
    private var replicas = (1 to N).map(_ => newReplica()).toSet
    private val seqNr = Iterator from 0
    private var replies = TreeMap.empty[Int, ReplyState]
    private var nextReply = 0

    override def supervisorStrategy = SupervisorStrategy.stoppingStrategy

    private def newReplica(): ActorRef =
      context.watch(context.actorOf(Replica.props))

    // schedule timeout messages for quiescent periods
    context.setReceiveTimeout(1.second)

    def receive = ({
      case cmd: Command =>
        val c = SeqCommand(seqNr.next, cmd, self)
        replicas foreach (_ ! c)
        replies += c.seq -> Unknown(5 seconds fromNow, Set.empty, replicas, (replicas.size + 1) / 2)
      case res: SeqResult if replies.contains(res.seq) && replicas.contains(res.replica) =>
        val prevState = replies(res.seq)
        val nextState = prevState.add(res)
        replies += res.seq -> nextState
      case Terminated(ref) =>
        replaceReplica(ref, terminate = false)
      case ReceiveTimeout =>
    }: Receive) andThen { _ =>
      doTimeouts()
      sendReplies()
      evictFinished()
    }

    private def doTimeouts(): Unit = {
      val now = Deadline.now
      val expired = replies.iterator.takeWhile(_._2.deadline <= now)
      for ((seq, state) <- expired) {
        state match {
          case Unknown(deadline, received, _, _) =>
            val forced = Known.fromUnknown(deadline, received)
            replies += seq -> forced
          case Known(deadline, reply, wrong, missing) =>
            replies += seq -> Known(deadline, reply, wrong, Set.empty)
        }
      }
    }

    @tailrec private def sendReplies(): Unit =
      replies.get(nextReply) match {
        case Some(k @ Known(_, reply, _, _)) =>
          reply.replyTo ! reply.res
          nextReply += 1
          sendReplies()
        case _ =>
      }

    @tailrec private def evictFinished(): Unit =
      replies.headOption match {
        case Some((seq, k @ Known(_, _, wrong, _))) if k.isFinished =>
          wrong foreach (replaceReplica(_, terminate = true))
          replies -= seq
          evictFinished()
        case _ =>
      }

    private def replaceReplica(r: ActorRef, terminate: Boolean): Unit =
      if (replicas contains r) {
        replicas -= r
        if (terminate) r ! PoisonPill
        val replica = newReplica()
        replicas.head ! SendInitialData(replica)
        replicas += replica
      }
  }

}