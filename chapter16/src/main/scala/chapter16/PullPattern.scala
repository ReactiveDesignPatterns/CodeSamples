/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter16

import java.math.{ MathContext, RoundingMode }

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

object PullPattern {

  final case class Job(id: Long, input: Int, replyTo: ActorRef)

  final case class JobResult(id: Long, report: BigDecimal)

  final case class WorkRequest(worker: ActorRef, items: Int)

  // #snip_16-1
  class Worker(manager: ActorRef) extends Actor {
    private val mc = new MathContext(100, RoundingMode.HALF_EVEN)
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
    }
  }

  // #snip_16-1

  // #snip_16-2
  class Manager extends Actor {

    private val works: Iterator[Job] =
      Iterator from 1 map (x ⇒ Job(x, x, self)) take 1000000

    private val aggregator: (BigDecimal, BigDecimal) ⇒ BigDecimal = (x: BigDecimal, y: BigDecimal) ⇒ x + y
    private val mc = new MathContext(10000, RoundingMode.HALF_EVEN)
    private var approximation = BigDecimal(0, mc)

    private var outstandingWork = 0

    (1 to 8) foreach (_ ⇒ context.actorOf(Props(new Worker(self))))

    def receive: Receive = {
      case WorkRequest(worker, items) ⇒
        works.toStream.take(items).foreach { job ⇒
          worker ! job
          outstandingWork += 1
        }
      case JobResult(id, report) ⇒
        approximation = aggregator(approximation, report)
        outstandingWork -= 1
        if (outstandingWork == 0 && works.isEmpty) {
          println("final result: " + approximation)
          context.system.terminate()
        }
    }
  }

  // #snip_16-2

  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("pi")
    sys.actorOf(Props(new Manager), "manager")
  }

}
