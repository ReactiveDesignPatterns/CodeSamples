/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter13

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ddata._
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.duration._

object MultiMasterCRDT {

  // #snip_13-10
  final case class Status(name: String)(
    _pred: ⇒ Set[Status], _succ: ⇒ Set[Status]) extends ReplicatedData {

    type T = Status

    def merge(that: Status): Status = mergeStatus(this, that)

    @volatile lazy val predecessors: Set[Status] = _pred
    @volatile lazy val successors: Set[Status] = _succ

  }

  val New: Status =
    Status("new")(Set.empty, Set(Scheduled, Cancelled))
  val Scheduled: Status =
    Status("scheduled")(Set(New), Set(Executing, Cancelled))
  val Executing: Status =
    Status("executing")(Set(Scheduled), Set(Aborted, Finished))
  val Finished: Status =
    Status("finished")(Set(Executing, Aborted), Set.empty)
  val Cancelled: Status =
    Status("cancelled")(Set(New, Scheduled), Set(Aborted))
  val Aborted: Status =
    Status("aborted")(Set(Cancelled, Executing), Set(Finished))
  // #snip_13-10

  // #snip_13-11
  def mergeStatus(left: Status, right: Status): Status = {
    /*
     * Keep the left Status in hand and determine whether it is a predecessor of
     * the candidate, moving on to the candidate’s successor if not successful.
     * The list of exclusions is used to avoid performing already determined
     * unsuccessful comparisons again.
     */
    def innerLoop(candidate: Status, exclude: Set[Status]): Status =
      if (isSuccessor(candidate, left, exclude)) {
        candidate
      } else {
        val nextExclude = exclude + candidate
        val branches =
          candidate.successors.map(succ ⇒ innerLoop(succ, nextExclude))
        branches.reduce((l, r) ⇒
          if (isSuccessor(l, r, nextExclude)) r else l)
      }

    def isSuccessor(
      candidate: Status,
      fixed:     Status, exclude: Set[Status]): Boolean =
      if (candidate == fixed) true
      else {
        val toSearch = candidate.predecessors -- exclude
        toSearch.exists(pred ⇒ isSuccessor(pred, fixed, exclude))
      }

    innerLoop(right, Set.empty)
  }

  // #snip_13-11

  object StorageComponent extends Key[ORMap[String, Status]]("StorageComponent")

  case class Submit(job: String)

  case class Cancel(job: String)

  case class Execute(job: String)

  case class Finish(job: String)

  case object PrintStatus

  // #snip_13-12
  class ClientInterface extends Actor with ActorLogging {
    private val replicator: ActorRef = DistributedData(context.system).replicator
    private implicit val cluster: Cluster = Cluster(context.system)

    def receive: Receive = {
      case Submit(job) ⇒
        log.info("submitting job {}", job)
        replicator ! Replicator.Update(
          StorageComponent,
          ORMap.empty[String, Status],
          Replicator.WriteMajority(5.seconds),
          Some(s"submit $job"))(_ + (job -> New))
      case Cancel(job) ⇒
        log.info("cancelling job {}", job)
        replicator ! Replicator.Update(
          StorageComponent,
          ORMap.empty[String, Status],
          Replicator.WriteMajority(5.seconds),
          Some(s"cancel $job"))(_ + (job -> Cancelled))
      case r: Replicator.UpdateResponse[_] ⇒
        log.info("received update result: {}", r)
      case PrintStatus ⇒
        replicator ! Replicator.Get(
          StorageComponent,
          Replicator.ReadMajority(5.seconds))
      case g: Replicator.GetSuccess[_] ⇒
        log.info("overall status: {}", g.get(StorageComponent))
    }
  }

  // #snip_13-12

  // #snip_13-13
  class Executor extends Actor with ActorLogging {
    private val replicator: ActorRef = DistributedData(context.system).replicator
    private implicit val cluster: Cluster = Cluster(context.system)

    private var lastState = Map.empty[String, Status]

    replicator ! Replicator.Subscribe(StorageComponent, self)

    def receive: Receive = {
      case Execute(job) ⇒
        log.info("executing job {}", job)
        replicator ! Replicator.Update(
          StorageComponent,
          ORMap.empty[String, Status],
          Replicator.WriteMajority(5.seconds), Some(job)) { map ⇒
            require(map.get(job).contains(New))
            map + (job -> Executing)
          }
      case Finish(job) ⇒
        log.info("job {} finished", job)
        replicator ! Replicator.Update(
          StorageComponent,
          ORMap.empty[String, Status],
          Replicator.WriteMajority(5.seconds))(_ + (job -> Finished))
      case Replicator.UpdateSuccess(StorageComponent, Some(job)) ⇒
        log.info("starting job {}", job)
      case r: Replicator.UpdateResponse[_] ⇒
        log.info("received update result: {}", r)
      case ch: Replicator.Changed[_] ⇒
        val current = ch.get(StorageComponent).entries
        for {
          (job, status) ← current.iterator
          if status == Aborted
          if !lastState.get(job).contains(Aborted)
        } {
          log.info("aborting job {}", job)
          lastState = current
        }
    }
  }

  // #snip_13-13

  val commonConfig: Config = ConfigFactory.parseString(
    """
    akka.actor.provider = akka.cluster.ClusterActorRefProvider
    akka.remote.netty.tcp {
      host = "127.0.0.1"
      port = 0
    }
    akka.cluster {
      gossip-interval = 100ms
      failure-detector {
        heartbeat-interval = 100ms
        acceptable-heartbeat-pause = 500ms
      }
      distributed-data.gossip-interval = 100ms
    }
    """)

  object sleep

  implicit object waitConvert extends DurationConversions.Classifier[sleep.type] {
    type R = Unit

    def convert(d: FiniteDuration): Unit = Thread.sleep(d.toMillis)
  }

  def main(args: Array[String]): Unit = {
    val sys1 = ActorSystem("MultiMasterCRDT", commonConfig)
    val addr1 = Cluster(sys1).selfAddress
    Cluster(sys1).join(addr1)

    val sys2 = ActorSystem("MultiMasterCRDT", commonConfig)
    Cluster(sys2).join(addr1)

    awaitMembers(sys1, 2)

    val clientInterface =
      sys1.actorOf(Props(new ClientInterface), "clientInterface")
    val executor = sys2.actorOf(Props(new Executor), "executor")

    clientInterface ! Submit("alpha")
    clientInterface ! Submit("beta")
    clientInterface ! Submit("gamma")
    clientInterface ! Submit("delta")
    1 second sleep
    executor ! Execute("alpha")
    executor ! Execute("gamma")
    clientInterface ! Cancel("delta")
    1 second sleep
    clientInterface ! Cancel("alpha")
    clientInterface ! Cancel("beta")
    executor ! Execute("beta")
    executor ! Execute("delta")
    1 second sleep
    clientInterface ! Cancel("gamma")
    1 second sleep
    executor ! Finish("gamma")
    3 seconds sleep
    clientInterface ! PrintStatus
    1 second sleep

    sys1.terminate()
    sys2.terminate()
  }

  private def awaitMembers(sys: ActorSystem, count: Int): Unit = {
    while (Cluster(sys).state.members.size < count) {
      Thread.sleep(500)
      print('.')
      Console.flush()
    }
    println("cluster started")
  }
}
