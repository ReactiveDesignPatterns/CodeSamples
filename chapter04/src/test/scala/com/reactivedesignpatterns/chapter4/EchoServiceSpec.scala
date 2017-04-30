package com.reactivedesignpatterns.chapter4

import scala.concurrent.duration.{ DurationInt, FiniteDuration }
import scala.util.Try
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }
import com.reactivedesignpatterns.Defaults._
import akka.actor.{ Actor, ActorRef, ActorSystem, Props, actorRef2Scala }
import akka.testkit.TestProbe
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import LatencyTestSupport._

object EchoServiceSpec {
  import EchoService._

  case class TestSLA(echo: ActorRef, n: Int, maxParallelism: Int, reportTo: ActorRef)
  case object AbortSLATest
  case class SLAResponse(timings: Seq[FiniteDuration], outstanding: Map[String, Timestamp])

  class ParallelSLATester extends Actor {

    def receive = {
      case TestSLA(echo, n, maxParallelism, reportTo) =>
        val receiver = context.actorOf(receiverProps(self))
        // prime the request pipeline
        val sendNow = Math.min(n, maxParallelism)
        val outstanding = Map.empty ++ (for (_ <- 1 to sendNow) yield sendRequest(echo, receiver))
        context.become(running(reportTo, echo, n - sendNow, receiver, outstanding, Nil))
    }

    def running(reportTo: ActorRef,
                echo: ActorRef,
                remaining: Int,
                receiver: ActorRef,
                outstanding: Map[String, Timestamp],
                timings: List[FiniteDuration]): Receive = {
      case TimedResponse(Response(r), d) =>
        val start = outstanding(r)
        val newOutstanding = outstanding - r + sendRequest(echo, receiver)
        val newTimings = (d - start) :: timings
        val newRemaining = remaining - 1
        if (newRemaining > 0)
          context.become(running(reportTo, echo, newRemaining, receiver, newOutstanding, newTimings))
        else
          context.become(finishing(reportTo, newOutstanding, newTimings))
      case AbortSLATest =>
        context.stop(self)
        reportTo ! SLAResponse(timings, outstanding)
    }

    def finishing(reportTo: ActorRef, outstanding: Map[String, Timestamp], timings: List[FiniteDuration]): Receive = {
      case TimedResponse(Response(r), d) =>
        val start = outstanding(r)
        val newOutstanding = outstanding - r
        val newTimings = (d - start) :: timings
        if (newOutstanding.isEmpty) {
          context.stop(self)
          reportTo ! SLAResponse(newTimings, newOutstanding)
        } else context.become(finishing(reportTo, newOutstanding, newTimings))
      case AbortSLATest =>
        context.stop(self)
        reportTo ! SLAResponse(timings, outstanding)
    }

    val idGenerator = Iterator from 1 map (i => s"test-$i")

    def sendRequest(echo: ActorRef, receiver: ActorRef): (String, Timestamp) = {
      val request = idGenerator.next
      val timestamp = Timestamp.now
      echo ! Request(request, receiver)
      request -> timestamp
    }
  }

  private def receiverProps(controller: ActorRef) = Props(new ParallelSLATestReceiver(controller))
  private case class TimedResponse(r: Response, d: Timestamp)

  // timestamp received replies in a dedicated actor to keep timing distortions low
  private class ParallelSLATestReceiver(controller: ActorRef) extends Actor {
    def receive = {
      case r: Response => controller ! TimedResponse(r, Timestamp.now)
    }
  }

}

class EchoServiceSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  import EchoService._
  import EchoServiceSpec._

  // implicitly picked up to create TestProbes, lazy to only start when used
  implicit lazy val system = ActorSystem("EchoServiceSpec", ConfigFactory.parseString("""
akka.actor.default-dispatcher.fork-join-executor.parallelism-max = 3
"""))

  /*
   * Discussion of the thread pool size configuration
   * 
   * When using the default configuration of 8 threads or more (depending on
   * the number of processor cores available) the three active actors in the
   * test will bounce frequently between threads, incurring CPU cache misses
   * and wake-up latencies from low-power states. Depending on the precise
   * scheduling patterns for each given run this can lead to variable increases
   * in response latency.
   * 
   * Configure a lower number than the number of active Actors means that
   * messages will be delayed by having to wait for their destination Actor
   * to be scheduled again.
   */

  override def afterAll(): Unit = {
    system.shutdown()
  }

  private def echoService(name: String): ActorRef = system.actorOf(Props[EchoService], name)

  "An EchoService" must {

    "reply correctly" in {
      val probe = TestProbe()
      val echo = echoService("replyCorrectly")
      echo ! Request("test", probe.ref)
      probe.expectMsg(1.second, Response("test"))
    }

    "keep its SLA" in {
      val probe = TestProbe()
      val echo = echoService("keepSLA")
      val N = 200
      val timings = for (i <- 1 to N) yield {
        val string = s"test$i"
        val start = Timestamp.now
        echo ! Request(string, probe.ref)
        probe.expectMsg(100.millis, s"test run $i", Response(string))
        val stop = Timestamp.now
        stop - start
      }
      // discard top 5%
      val sorted = timings.sorted
      val ninetyfifthPercentile = sorted.dropRight(N * 5 / 100).last
      info(s"SLA min=${sorted.head} max=${sorted.last} 95th=$ninetyfifthPercentile")
      ninetyfifthPercentile should be < 1.millisecond
    }

    "keep its SLA when used in parallel with Futures" in {
      implicit val timeout = Timeout(100.millis)
      import system.dispatcher
      val echo = echoService("keepSLAfuture")
      val N = 10000
      val timingFutures = for (i <- 1 to N) yield {
        val string = s"test$i"
        val start = Timestamp.now
        (echo ? (Request(string, _))) collect {
          case Response(`string`) => Timestamp.now - start
        }
      }
      val futureOfTimings = Future.sequence(timingFutures)
      val timings = Await.result(futureOfTimings, 5.seconds)
      // discard top 5%
      val sorted = timings.sorted
      val ninetyfifthPercentile = sorted.dropRight(N * 5 / 100).last
      info(s"SLA min=${sorted.head} max=${sorted.last} 95th=$ninetyfifthPercentile")
      ninetyfifthPercentile should be < 100.milliseconds
    }

    "keep its SLA when used in parallel" in {
      val echo = echoService("keepSLAparallel")
      val probe = TestProbe()
      val N = 10000
      val maxParallelism = 500
      val controller = system.actorOf(Props[ParallelSLATester], "keepSLAparallelController")
      controller ! TestSLA(echo, N, maxParallelism, probe.ref)
      val result = Try(probe.expectMsgType[SLAResponse]).recover {
        case ae: AssertionError =>
          controller ! AbortSLATest
          val result = probe.expectMsgType[SLAResponse]
          info(s"controller timed out, state so far is $result")
          throw ae
      }.get
      // discard top 5%
      val sorted = result.timings.sorted
      val ninetyfifthPercentile = sorted.dropRight(N * 5 / 100).last
      info(s"SLA min=${sorted.head} max=${sorted.last} 95th=$ninetyfifthPercentile")
      ninetyfifthPercentile should be < 2.milliseconds
    }

  }

  "An EchoService (with LatencyTestSupport)" should {

    "keep its SLA" in {
      implicit val timeout = Timeout(5.seconds)
      import system.dispatcher
      val echo = echoService("keepSLAwithSupport")
      val latencySupport = new LatencyTestSupport(system)
      val latencies = latencySupport.measure(count = 10000, maxParallelism = 500) { i =>
        val message = s"test$i"
        SingleResult((echo ? (Request(message, _))), Response(message))
      }
      val lat = Await.result(latencies, 20.seconds)
      info(s"latency info: $lat")
      lat.failureCount should be(0)
      lat.quantile(0.99) should be < 10.milliseconds
    }

  }

}