/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter16

import java.math.MathContext
import java.math.RoundingMode

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

object PullPattern {

  case class Job(id: Long, input: Int, replyTo: ActorRef)
  case class JobResult(id: Long, report: BigDecimal)

  case class WorkRequest(worker: ActorRef, items: Int)

  class Manager extends Actor {

    val workStream: Iterator[Job] =
      Iterator from 1 map (x => Job(x, x, self)) take 1000000

    val aggregator = (x: BigDecimal, y: BigDecimal) => x + y
    var approximation = BigDecimal(0, new MathContext(10000, RoundingMode.HALF_EVEN))

    var outstandingWork = 0

    (1 to 8) foreach (_ => context.actorOf(Props(new Worker(self))))

    def receive = {
      case WorkRequest(worker, items) =>
        workStream.take(items).foreach {
          job =>
            worker ! job
            outstandingWork += 1
        }
      case JobResult(id, report) =>
        approximation = aggregator(approximation, report)
        outstandingWork -= 1
        if (outstandingWork == 0 && workStream.isEmpty) {
          println("final result: " + approximation)
          context.system.terminate()
        }
    }
  }

  class Worker(manager: ActorRef) extends Actor {
    val mc = new MathContext(100, RoundingMode.HALF_EVEN)
    val plus = BigDecimal(1, mc)
    val minus = BigDecimal(-1, mc)

    var requested = 0
    def request(): Unit =
      if (requested < 5) {
        manager ! WorkRequest(self, 10)
        requested += 10
      }

    request()

    def receive = {
      case Job(id, data, replyTo) =>
        requested -= 1
        request()
        val sign = if ((data & 1) == 1) plus else minus
        val result = sign / data
        replyTo ! JobResult(id, result)
    }
  }

  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("pi")
    sys.actorOf(Props(new Manager), "manager")
  }

}
