/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter12

import sbt.IO
import play.api.libs.json._
import java.io.File
import akka.actor._
import scala.collection.mutable.Queue
import akka.cluster.Cluster
import scala.concurrent.duration._
import scala.collection.immutable.TreeMap
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.util.Random
import scala.annotation.tailrec
import akka.contrib.pattern.ClusterSingletonManager
import akka.contrib.pattern.ClusterSingletonProxy
import com.typesafe.config.ConfigFactory

object ActivePassive {
  import ReplicationProtocol._
  import Persistence._

  private case class Replicate(seq: Int, key: String, value: JsValue, replyTo: ActorRef)
  private case class Replicated(seq: Int)
  private case object Tick

  private case class TakeOver(replyTo: ActorRef)
  private case class InitialState(map: Map[String, JsValue], seq: Int)

  class Active(localReplica: ActorRef, replicationFactor: Int, maxQueueSize: Int) extends Actor with Stash with ActorLogging {
    private val MaxOutstanding = maxQueueSize / 2

    private var theStore = Map.empty[String, JsValue]
    private var seqNr = Iterator from 0
    private val toReplicate = Queue.empty[Replicate]
    private var replicating = TreeMap.empty[Int, (Replicate, Int)]

    val cluster = Cluster(context.system)

    import context.dispatcher
    val timer = context.system.scheduler.schedule(1.second, 1.second, self, Tick)
    override def postStop() = timer.cancel()

    log.info("taking over from local replica")
    localReplica ! TakeOver(self)
    
    def receive = {
      case InitialState(m, s) =>
        theStore = m
        seqNr = Iterator from s
        context.become(running)
        unstashAll()
      case _ => stash()
    }

    val running: Receive = {
      case Put(key, value, replyTo) =>
        if (toReplicate.size < MaxOutstanding) {
          toReplicate.enqueue(Replicate(seqNr.next, key, value, replyTo))
          replicate()
        } else {
          replyTo ! PutRejected(key, value)
        }
      case Get(key, replyTo) =>
        replyTo ! GetResult(key, theStore get key)
      case Tick =>
        replicating.valuesIterator foreach {
          case (replicate, count) => disseminate(replicate)
        }
      case Replicated(confirm) =>
        replicating.get(confirm) match {
          case None => // already removed
          case Some((rep, 1)) =>
            replicating -= confirm
            theStore += rep.key -> rep.value
            rep.replyTo ! PutConfirmed(rep.key)
          case Some((rep, n)) =>
            replicating += confirm -> (rep, n - 1)
        }
        replicate()
    }

    private def replicate(): Unit =
      if (replicating.size < MaxOutstanding && toReplicate.nonEmpty) {
        val r = toReplicate.dequeue()
        replicating += r.seq -> (r, replicationFactor)
        disseminate(r)
      }
    private def disseminate(r: Replicate): Unit = {
      val req = r.copy(replyTo = self)
      cluster.state.members.foreach(m => replicaOn(m.address) ! req)
    }
    private def replicaOn(addr: Address): ActorSelection =
      context.actorSelection(localReplica.path.toStringWithAddress(addr))
  }

  private case class GetSingle(seq: Int, replyTo: ActorRef)
  private case class GetFull(replyTo: ActorRef)
  private case object DoConsolidate

  class Passive(askAroundCount: Int, askAroundInterval: FiniteDuration) extends Actor with ActorLogging {
    private var theStore: Map[String, JsValue] = _
    private var expectedSeq: Int = 0
    private val applied = Queue.empty[Replicate]
    private var waiting = TreeMap.empty[Int, Replicate]

    val name = self.path.address.toString
    val cluster = Cluster(context.system)
    val random = new Random

    readPersisted(name) match {
      case Database(s, kv) =>
        theStore = kv
        expectedSeq = s + 1
    }
    log.info("started at sequence {}", expectedSeq)
    
    override def postStop(): Unit = {
      log.info("stopped at sequence {}", expectedSeq)
    }

    def receive = {
      case TakeOver(active) =>
        active ! InitialState(theStore, expectedSeq)
      case Replicate(s, _, _, replyTo) if s < expectedSeq =>
        replyTo ! Replicated(s)
      case r: Replicate =>
        waiting += r.seq -> r
        consolidate()
      case GetSingle(s, replyTo) =>
        if (applied.nonEmpty && applied.head.seq <= s && applied.last.seq >= s)
          replyTo ! applied.find(_.seq == s).get
        else if (s < expectedSeq) replyTo ! InitialState(theStore, expectedSeq)
      case GetFull(replyTo) =>
        replyTo ! InitialState(theStore, expectedSeq)
      case InitialState(m, s) if s > expectedSeq =>
        theStore = m
        expectedSeq = s + 1
        persist(name, s, m)
        waiting.to(s).valuesIterator foreach (r => r.replyTo ! Replicated(r.seq))
        waiting = waiting.from(expectedSeq)
        consolidate()
      case DoConsolidate =>
        // this is scheduled when asking around for state so that eventually we
        // will get all updates
        consolidate()
    }

    private def consolidate(): Unit = {
      // calculate applicable prefix length
      def matches(p: (Int, Int)) = p._1 == p._2
      val prefix = waiting.keysIterator.zip(Iterator from expectedSeq).takeWhile(matches).size

      if (prefix > 0) {
        waiting.valuesIterator.take(prefix) foreach { replicate =>
          theStore += replicate.key -> replicate.value
          expectedSeq = replicate.seq + 1
          persist(name, replicate.seq, theStore)
          replicate.replyTo ! Replicated(replicate.seq)
          applied.enqueue(replicate)
        }
        waiting = waiting.drop(prefix)
      }
      
      // cap the size of the applied buffer
      applied.drop(Math.max(0, applied.size - 10))

      // check if we fell behind by too much
      if (waiting.lastKey - expectedSeq > 10) {
        val outstanding = (expectedSeq to waiting.lastKey).iterator.filterNot(waiting.contains).toList
        if (outstanding.size <= 3) outstanding foreach askAround
        else askAroundFullState()
        context.system.scheduler.scheduleOnce(askAroundInterval, self, DoConsolidate)(context.dispatcher)
      }
    }

    private def getMembers(n: Int): Seq[Address] = {
      random.shuffle(cluster.state.members.iterator.map(_.address).toSeq).take(n)
    }
    private def askAround(seq: Int): Unit = {
      getMembers(askAroundCount).foreach(addr => replicaOn(addr) ! GetSingle(seq, self))
    }
    private def askAroundFullState(): Unit = {
      getMembers(1).foreach(addr => replicaOn(addr) ! GetFull(self))
    }
    private def replicaOn(addr: Address): ActorSelection =
      context.actorSelection(self.path.toStringWithAddress(addr))
  }
  
  val commonConfig = ConfigFactory.parseString("""
    akka.actor.provider = akka.cluster.ClusterActorRefProvider
    akka.cluster {
      gossip-interval = 100ms
      failure-detector {
        heartbeat-interval = 100ms
        acceptable-heartbeat-pause = 500ms
      }
    }
    """)
  def roleConfig(name: String) = ConfigFactory.parseString(s"""akka.cluster.roles = ["$name"]""")

  def start(n: Int): ActorSystem = {
    val system = ActorSystem(s"node$n", roleConfig("backend") withFallback commonConfig)
    val localReplica = system.actorOf(Props(new Passive(3, 3.seconds)), "passive")
    val managerProps =
      ClusterSingletonManager.props(Props(new Active(localReplica, 2, 10)), "active", PoisonPill, Some("backend"))
    val manager = system.actorOf(managerProps, "activeManager")
    system
  }

  def main(args: Array[String]): Unit = {
    val systems = Array.tabulate(5)(start(_))
    val seedNode = Cluster(systems(0)).selfAddress
    systems foreach (Cluster(_).join(seedNode))

    val sys = ActorSystem("ActivePassive", commonConfig)
    Cluster(sys).join(seedNode)
    
    while (Cluster(sys).state.members.size < 6) {
      Thread.sleep(500)
      print('.')
      Console.flush()
    }
    println("cluster started")
    
    val path = sys / "user" / "activeManager" / "active"
    val proxy = sys.actorOf(ClusterSingletonProxy.props(path.toString, Some("backend")))
    
    sys.actorOf(Props(new UseStorage(proxy)), "useStorage") ! Run
    
    while (!terminate) {
      
    }
  }
  
  private case object Run
  private case object Stop
  @volatile private var terminate = false
  
  private class UseStorage(db: ActorRef) extends Actor {
    def receive = {
      case Run =>
        
        self ! Run
      case Stop => context.stop(self)
    }
    override def postStop(): Unit = terminate = true
  }

  /*
   * Problems:
   * - when after TakeOver the localReplica gets a full map that has newer info, we potentially
   *   accepted conflicting writes
   * - after TakeOver we should probably do a reconciliation phase that brings all known replicas
   *   up to speed
   * - but if a replica with newer data joins later then we’ll need to wipe its contents and roll
   *   it back because a different universe has won
   * - in general there is the question of when to wipe a local copy that gets kicked out: we
   *   want that for cases where it was in a losing partition but NOT when the whole cluster goes
   *   down
   * - I guess that this can only be reconciled later when joining things back together.
   */

}
