/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter12

import akka.actor._
import play.api.libs.json.JsValue
import scala.collection.immutable.TreeMap
import scala.concurrent.duration._

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
    def isKnown: Boolean
    def isFinished: Boolean = missing.isEmpty
  }
  private case class Unknown(deadline: Deadline, replies: Set[SeqResult], missing: Set[ActorRef]) extends ReplyState {
    override def add(res: SeqResult): ReplyState = {
      val quorum = (missing.size + 1) / 2
      val nextReplies = replies + res
      if (nextReplies.size >= quorum) {
        val answer = replies.toSeq.groupBy(_.res).collectFirst { case (k, s) if s.size >= quorum => s.head }
        if (answer.isDefined) {
          val right = answer.get
          val wrong = replies.collect { case SeqResult(_, res, replica, _) if res != right => replica }
          Known(deadline, right, wrong, missing - res.replica)
        } else Unknown(deadline, nextReplies, missing - res.replica)
      } else Unknown(deadline, nextReplies, missing - res.replica)
    }
    override def isKnown = false
  }
  private case class Known(deadline: Deadline, reply: SeqResult, wrong: Set[ActorRef], missing: Set[ActorRef]) extends ReplyState {
    override def add(res: SeqResult): ReplyState = {
      val nextWrong = if (res.res == reply.res) wrong else wrong + res.replica
      Known(deadline, reply, nextWrong, missing - res.replica)
    }
    override def isKnown = true
  }

  class Coordinator(N: Int) extends Actor {
    private var replicas = (1 to N).map(_ => context.actorOf(Replica.props)).toSet
    private val seqNr = Iterator from 0
    private var replies = TreeMap.empty[Int, ReplyState]
    private var nextReply = 0

    context.setReceiveTimeout(1.second)

    def receive = {
      case cmd: Command =>
        val c = SeqCommand(seqNr.next, cmd, self)
        replicas foreach (_ ! c)
        replies += c.seq -> Unknown(5 seconds fromNow, Set.empty, replicas)
        doTimeouts()
      case res: SeqResult if replies.contains(res.seq) && replicas.contains(res.replica) =>
        val prevState = replies(res.seq)
        val nextState = prevState.add(res)
        // potentially send reply if quorum of replies has been received now
        nextState match {
          case Known(seq, reply, _, _) if !prevState.isKnown && seq == nextReply =>
            reply.replyTo ! reply.res
            nextReply += 1
          case _ =>
        }
        // clean up state
        if (nextState.isFinished) {
          dispose(nextState)
          replies -= res.seq
        } else {
          replies += res.seq -> nextState
          doTimeouts()
        }
      case ReceiveTimeout => doTimeouts()
    }

    private def doTimeouts(): Unit = {
      val now = Deadline.now
      val expired = replies.iterator.takeWhile(_._2.deadline <= now)
      expired.map(_._2).foreach(dispose)
    }

    /**
     * The given reply state has been removed from the replies map and is now
     * being disposed of. This means that we need to act upon wrong replies
     * from replicas.
     *
     * If there are replicas for which no reply has been recorded yet, we
     * ignore them. If they reply incorrectly later they will be replaced then.
     * GC pauses are tolerated: do not kick out replicas just for being slow.
     */
    private def dispose(state: ReplyState): Unit =
      state match {
        case Unknown(_, replies, _) =>
          // did not reach consensus on this one, pick simple majority
          val counts = replies.toList.groupBy(_.res)
          val biggest = counts.iterator.map(_._2.size).max
          val winners = counts.collectFirst {
            case (res, win) if win.size == biggest => win
          }.get
          val losers = replicas -- winners.iterator.map(_.replica).toSet
          losers foreach replaceReplica
        case Known(_, _, wrong, _) =>
          wrong foreach replaceReplica
      }

    private def replaceReplica(r: ActorRef): Unit = {
      replicas -= r
      r ! PoisonPill
      val newReplica = context.actorOf(Replica.props)
      replicas.head ! SendInitialData(newReplica)
      replicas += newReplica
    }
  }

}