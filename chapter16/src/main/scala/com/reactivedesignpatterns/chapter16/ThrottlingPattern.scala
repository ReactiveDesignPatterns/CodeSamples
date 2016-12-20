/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter16

import java.math.MathContext
import java.math.RoundingMode
import akka.actor._
import akka.stream.scaladsl._
import akka.pattern.extended.ask
import scala.collection.immutable.Queue
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.util.{ Failure, Success }

object ThrottlingPattern {

  /*
   * compile-time constant: set to true to enable log statements.
   * when set to false, compiler will elide log statements from code.
   */
  final val Debug = false

  case class Job(id: Long, input: Int, replyTo: ActorRef)
  case class JobRejected(id: Long)
  case class JobResult(id: Long, report: BigDecimal)

  case class WorkRequest(worker: ActorRef, items: Int)
  case class DummyWork(count: Int)

  class Manager extends Actor {

    var workQueue = Queue.empty[Job]
    var requestQueue = Queue.empty[WorkRequest]

    (1 to 8) foreach (_ => context.actorOf(Props(new Worker(self)).withDispatcher(context.props.dispatcher)))

    def receive = {
      case job @ Job(id, _, replyTo) =>
        if (requestQueue.isEmpty) {
          if (workQueue.size < 10000) workQueue :+= job
          else {
            if (Debug) println(s"${System.nanoTime}: queue overrun")
            replyTo ! JobRejected(id)
          }
        } else {
          val WorkRequest(worker, items) = requestQueue.head
          worker ! job
          if (items > 1) worker ! DummyWork(items - 1)
          requestQueue = requestQueue.drop(1)
        }
      case wr @ WorkRequest(worker, items) =>
        if (Debug) println(s"${System.nanoTime}: received WorkRequest($items)")
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
      if (requested < 50) {
        manager ! WorkRequest(self, 100)
        requested += 100
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
    val empty = Report(0, 0, BigDecimal(0, mc))
  }

  class WorkSource extends Actor {
    val N = 1000000
    var start: Deadline = _

    val workStream: Iterator[Job] =
      Iterator from 1 map (x => Job(x, x, self)) take N

    var approximation = Report.empty
    var outstandingWork = 0

    def receive = {
      case WorkRequest(worker, items) =>
        if (start == null) start = Deadline.now
        workStream.take(items).foreach { job =>
          worker ! job
          outstandingWork += 1
        }
      case JobResult(id, report) => registerReport(Report.success(report))
      case JobRejected(id)       => registerReport(Report.failure)
    }

    def registerReport(r: Report) = {
      approximation += r
      outstandingWork -= 1
      if (outstandingWork == 0 && workStream.isEmpty) {
        println("final result: " + approximation)
        val stop = Deadline.now
        val interval = stop - start
        val rate = N * 1000.0 / interval.toMillis
        println(s"elapsed: $interval ($rate / sec)")
        context.system.terminate()
      }
    }
  }

  class CalculatorClient(workSource: ActorRef, calculator: ActorRef, ratePerSecond: Long, bucketSize: Int, batchSize: Int) extends Actor {
    def now() = System.nanoTime()
    val nanoSecondsBetweenTokens = 1000000000L / ratePerSecond

    var tokenBucket = bucketSize
    var lastTokenTime = now()

    def refillBucket(time: Long): Unit = {
      val accrued = (time - lastTokenTime) * ratePerSecond / 1000000000L
      if (tokenBucket + accrued >= bucketSize) {
        tokenBucket = bucketSize
        lastTokenTime = time
      } else {
        tokenBucket += accrued.toInt
        lastTokenTime += accrued * nanoSecondsBetweenTokens
      }
    }
    def consumeToken(time: Long): Unit = {
      refillBucket(time)
      tokenBucket -= 1
    }

    var requested = 0
    def request(time: Long): Unit =
      if (tokenBucket - requested >= batchSize) {
        sendRequest(time, batchSize)
      } else if (requested == 0) {
        if (tokenBucket > 0) {
          sendRequest(time, tokenBucket)
        } else {
          val timeForNextToken = lastTokenTime + nanoSecondsBetweenTokens - time
          context.system.scheduler.scheduleOnce(timeForNextToken.nanos, workSource, WorkRequest(self, 1))(context.dispatcher)
          requested = 1
          if (Debug) println(s"$time: request(1) scheduled for ${time + timeForNextToken}")
        }
      } else if (Debug) println(s"$time: not requesting (requested=$requested tokenBucket=$tokenBucket)")
    def sendRequest(time: Long, items: Int): Unit = {
      if (Debug) println(s"$time: requesting $items items (requested=$requested tokenBucket=$tokenBucket)")
      workSource ! WorkRequest(self, items)
      requested += items
    }

    request(lastTokenTime)

    def receive = {
      case job: Job =>
        val time = now()
        if (Debug) if (requested == 1) println(s"$time: received job")
        consumeToken(time)
        requested -= 1
        request(time)
        calculator ! job
    }
  }

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString("""
akka.scheduler.tick-duration=1ms
worker-dispatcher {
  executor = "thread-pool-executor"
  thread-pool-executor {
    core-pool-size-min = 9
    core-pool-size-max = 9
  }
}
""")
    implicit val sys = ActorSystem("pi", config)
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(10.seconds)
    import sys.dispatcher

    val source = sys.actorOf(Props(new WorkSource), "workSource")
    val manager = sys.actorOf(Props(new Manager).withDispatcher("worker-dispatcher"), "manager")

    // warm up the engine
    Source(1 to 100000)
      .mapAsyncUnordered(1000)(i => manager ? (Job(i, i, _)))
      .runWith(Sink.ignore)
      .onComplete {
        case Failure(ex) => sys.terminate()
        case Success(_) =>
          // then run the actual computation
          println("starting the computation")
          sys.actorOf(Props(new CalculatorClient(source, manager, 50000, 1000, 100)), "client")
      }
  }

}
