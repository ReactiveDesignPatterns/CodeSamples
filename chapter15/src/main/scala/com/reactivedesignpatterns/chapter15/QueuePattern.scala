/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter15

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
  case class Result(id: Long, report: BigDecimal)

  case class WorkRequest(worker: ActorRef, items: Int)

  class Manager extends Actor {

    var workQueue = Queue.empty[Job]
    var requestQueue = Queue.empty[ActorRef]

    (1 to 8) foreach (_ => context.actorOf(Props(new Worker(self))))

    def receive = {
      case job @ Job(id, _, replyTo) =>
        if (requestQueue.isEmpty) {
          if (workQueue.size < 1000) workQueue :+= job
          else replyTo ! JobRejected(id)
        } else {
          requestQueue.head ! job
          requestQueue = requestQueue.drop(1)
        }
      case WorkRequest(worker, items) =>
        if (workQueue.isEmpty) {
          if (!requestQueue.contains(worker)) requestQueue :+= worker
        } else {
          workQueue.iterator.take(items).foreach(job => worker ! job)
          workQueue = workQueue.drop(items)
        }
    }
  }

  class Worker(manager: ActorRef) extends Actor {
    val mc = new MathContext(100, RoundingMode.HALF_EVEN)
    val plus = BigDecimal(1, mc)
    val minus = BigDecimal(-1, mc)

    manager ! WorkRequest(self, 1)

    def receive = {
      case Job(id, data, replyTo) =>
        manager ! WorkRequest(self, 1)
        val sign = if ((data & 1) == 1) plus else minus
        val result = sign / data
        replyTo ! Result(id, result)
    }
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
            case Result(_, report) => report
            case _                 => BigDecimal(0)
          })
    Future.reduce(futures)(_ + _)
      .map(x => println("final result: " + x * 4))
      .recover {
        case ex =>
          ex.printStackTrace()
      }
      .foreach(_ => sys.terminate())
  }

}
