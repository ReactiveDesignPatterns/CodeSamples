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
import akka.pattern.extended.ask
import scala.collection.immutable.Queue
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.Await
import java.util.concurrent.TimeoutException

object QueuePattern {

  case class Job(id: Long, input: Int, replyTo: ActorRef)
  case class JobRejected(id: Long)
  case class JobResult(id: Long, report: BigDecimal)

  case class WorkRequest(worker: ActorRef, items: Int)
  case class DummyWork(count: Int)

  class Manager extends Actor {

    var workQueue = Queue.empty[Job]
    var requestQueue = Queue.empty[WorkRequest]

    (1 to 8) foreach (_ => context.actorOf(Props(new Worker(self))))

    def receive = {
      case job @ Job(id, _, replyTo) =>
        if (requestQueue.isEmpty) {
          if (workQueue.size < 1000) workQueue :+= job
          else replyTo ! JobRejected(id)
        } else {
          val WorkRequest(worker, items) = requestQueue.head
          worker ! job
          if (items > 1) worker ! DummyWork(items - 1)
          requestQueue = requestQueue.drop(1)
        }
      case wr @ WorkRequest(worker, items) =>
        if (workQueue.isEmpty) {
          if (!requestQueue.contains(worker)) requestQueue :+= wr
        } else {
          workQueue.iterator.take(items).foreach(job => worker ! job)
          if (workQueue.size < items) worker ! DummyWork(items - workQueue.size)
          workQueue = workQueue.drop(items)
        }
    }
  }

  val mc = new MathContext(100, RoundingMode.HALF_EVEN)

  class Worker(manager: ActorRef) extends Actor {
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
      case DummyWork(count) =>
        requested -= count
        request()
    }
  }

  case class Report(success: Int, failure: Int, value: BigDecimal) {
    def +(other: Report) =
      Report(success + other.success, failure + other.failure, value + other.value)
  }
  object Report {
    def success(v: BigDecimal) = Report(1, 0, v)
    val failure = Report(0, 1, BigDecimal(0, mc))
  }

  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("pi")
    import sys.dispatcher
    val calculator = sys.actorOf(Props(new Manager), "manager")
    implicit val timeout = Timeout(10.seconds)
    val futures =
      (1 to 1000000).map(i =>
        (calculator ? (Job(i, i, _)))
          .collect {
            case JobResult(_, report) => Report.success(report)
            case _                    => Report.failure
          })
    Future.reduce(futures)(_ + _)
      .map(x => println(s"final result: $x"))
      .recover {
        case ex =>
          ex.printStackTrace()
      }
      .foreach(_ => sys.terminate())
  }

}
