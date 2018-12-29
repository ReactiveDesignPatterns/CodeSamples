/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter16

import java.math.{ MathContext, RoundingMode }
import java.util.concurrent.{ ThreadLocalRandom, TimeoutException }

import akka.actor._
import akka.pattern.extended.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.collection.immutable.Queue
import scala.concurrent.Await
import scala.concurrent.duration._

object DropPatternWithProtection {

  final case class Job(id: Long, input: Int, replyTo: ActorRef)

  final case class JobRejected(id: Long)

  final case class JobResult(id: Long, report: BigDecimal)

  final case class WorkRequest(worker: ActorRef, items: Int)

  final case class DummyWork(count: Int)

  final case class GetIncomingRef(replyTo: ActorRef)

  //#snip_1
  private final case class WorkEnvelope(job: Job) {
    @volatile var consumed = false
  }

  private class IncomingQueue(manager: ActorRef) extends Actor {
    private var workQueue: Queue[WorkEnvelope] = Queue.empty[WorkEnvelope]

    def receive: Receive = {
      case job: Job ⇒
        workQueue = workQueue.dropWhile(_.consumed)
        if (workQueue.size < 1000) {
          val envelope = WorkEnvelope(job)
          workQueue :+= envelope
          manager ! envelope
        }
    }
  }

  //#snip_1

  class Protector extends Actor {

    private val manager: ActorRef = context.actorOf(Props(new Manager), "manager")

    private val incomingQueue: ActorRef = context.actorOf(
      Props(new IncomingQueue(manager))
        .withMailbox("bounded-mailbox"), "incomingQueue")

    def receive: Receive = {
      case GetIncomingRef(replyTo) ⇒ replyTo ! incomingQueue
    }
  }

  class Manager extends Actor {

    private var workQueue: Queue[Job] = Queue.empty[Job]
    private var requestQueue: Queue[WorkRequest] = Queue.empty[WorkRequest]

    private val queueThreshold = 1000
    private val dropThreshold = 1500

    def random: ThreadLocalRandom = ThreadLocalRandom.current

    def shallEnqueue(atSize: Int): Boolean =
      (atSize < queueThreshold) || {
        val dropFactor = (atSize - queueThreshold) >> 6
        random.nextInt(dropFactor + 2) == 0
      }

    (1 to 8) foreach (_ ⇒ context.actorOf(Props(new Worker(self))))

    def receive: Receive = {
      case envelope @ WorkEnvelope(job @ Job(id, _, replyTo)) ⇒
        envelope.consumed = true
        if (requestQueue.isEmpty) {
          val atSize = workQueue.size
          if (shallEnqueue(atSize)) workQueue :+= job
          else if (atSize < dropThreshold) replyTo ! JobRejected(id)
        } else {
          val WorkRequest(worker, items) = requestQueue.head
          worker ! job
          if (items > 1) worker ! DummyWork(items - 1)
          requestQueue = requestQueue.drop(1)
        }
      case wr @ WorkRequest(worker, items) ⇒
        if (workQueue.isEmpty) {
          requestQueue :+= wr
        } else {
          workQueue.iterator.take(items).foreach(job ⇒ worker ! job)
          if (workQueue.size < items) worker ! DummyWork(items - workQueue.size)
          workQueue = workQueue.drop(items)
        }
    }
  }

  val mc = new MathContext(100, RoundingMode.HALF_EVEN)

  class Worker(manager: ActorRef) extends Actor {
    private val plus = BigDecimal(1, mc)
    private val minus = BigDecimal(-1, mc)

    private var requested = 0

    def request(): Unit =
      if (requested < 5) {
        manager ! WorkRequest(self, 10)
        requested += 10
      }

    request()

    def receive: Receive = {
      case Job(id, data, replyTo) ⇒
        requested -= 1
        request()
        val sign = if ((data & 1) == 1) plus else minus
        val result = sign / data
        replyTo ! JobResult(id, result)
      case DummyWork(count) ⇒
        requested -= count
        request()
    }
  }

  final case class Report(success: Int, failure: Int, dropped: Int, value: BigDecimal) {
    def +(other: Report) =
      Report(success + other.success, failure + other.failure, dropped + other.dropped, value + other.value)
  }

  object Report {
    def success(v: BigDecimal) = Report(1, 0, 0, v)

    val failure = Report(0, 1, 0, BigDecimal(0, mc))
    val dropped = Report(0, 0, 1, BigDecimal(0, mc))
    val empty = Report(0, 0, 0, BigDecimal(0, mc))
  }

  def main(args: Array[String]): Unit = {
    //TODO with config file
    val config = ConfigFactory.parseString(
      """
        |bounded-mailbox {
        | mailbox-type = "akka.dispatch.BoundedMailbox"
        | mailbox-capacity = 1000
        | mailbox-push-timeout-time = 0s
        |}
      """.stripMargin)
    implicit val sys: ActorSystem = ActorSystem("pi", config)
    import sys.dispatcher
    implicit val timeout: Timeout = Timeout(1.seconds)
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val protector = sys.actorOf(Props(new Protector), "protector")
    val calculator = Await.result((protector ? GetIncomingRef).mapTo[ActorRef], 1.second)

    Source(1 to 10000000)
      // experiment with the parallelism number to see dropping in effect
      .mapAsyncUnordered(100000) { i ⇒
        (calculator ? (Job(i, i, _)))
          .collect {
            case JobResult(_, report) ⇒ Report.success(report)
            case _                    ⇒ Report.failure
          }
          .recover {
            case _: TimeoutException ⇒ Report.dropped
          }
      }
      .runFold(Report.empty)(_ + _)
      .map(x ⇒ println(s"final result: $x"))
      .recover {
        case ex ⇒
          ex.printStackTrace()
      }
      .foreach(_ ⇒ sys.terminate())
  }

}
