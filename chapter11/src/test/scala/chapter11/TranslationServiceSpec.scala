/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.testkit.TestProbe
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }
import org.scalatest.concurrent.Eventually

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration._
import scala.concurrent.rdpextras.SynchronousEventLoop
import scala.util.Success

object TranslationServiceSpec {
  import TranslationService._

  // #snip_11-20
  case object ExpectNominal
  case object ExpectError
  case class Unexpected(msg: Any)

  class MockV1(reporter: ActorRef) extends Actor {
    def receive: Receive = initial

    override def unhandled(msg: Any): Unit = {
      reporter ! Unexpected(msg)
    }

    val initial: Receive = {
      case ExpectNominal ⇒ context.become(expectingNominal)
      case ExpectError   ⇒ context.become(expectingError)
    }

    def expectingNominal: Receive = {
      case TranslateV1("sv:en:Hur mår du?", replyTo) ⇒
        replyTo ! "How are you?"
        context.become(initial)
    }

    def expectingError: Receive = {
      case TranslateV1(other, replyTo) ⇒
        replyTo ! s"error:cannot parse input '$other'"
        context.become(initial)
    }
  }
  // #snip_11-20

  def mockV1props(reporter: ActorRef): Props = Props(new MockV1(reporter))

}

class TranslationServiceSpec extends WordSpec with Matchers with Eventually with BeforeAndAfterAll {
  import TranslationServiceSpec._

  "A TranslationService" should {

    "correctly translate from Swedish" when {

      "using SynchronousEventLoop" in {
        // #snip_11-10
        val tr = new TranslationService
        val input = "Hur mår du?"
        val output = "How are you?"
        val ec = SynchronousEventLoop
        val future = tr.translate(input, ec)
        future.value.get should be(Success(output))
        // #snip_11-10
      }

      "using Await.result" in {
        val tr = new TranslationService
        val input = "Hur mår du?"
        val output = "How are you?"
        val future = tr.translate(input)
        Await.result(future, 1.second) should be(output)
      }

      "using eventually()" in {
        // #snip_11-12
        val tr = new TranslationService
        val input = "Hur mår du?"
        val output = "How are you?"
        val future = tr.translate(input)
        eventually {
          future.value.get should be(Success(output))
        }
        // #snip_11-12
      }

    }

    "not respond immediately when running asynchronously" in {
      val tr = new TranslationService
      val input = "Hur mår du?"
      val output = "How are you?"
      val ec = ExecutionContext.global
      val future = tr.translate(input, ec)
      future.value should be(None)
      Await.result(future, 1.second) should be(output)
    }

  }

  implicit val system: ActorSystem = ActorSystem("TranslationServiceSpec")
  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }

  "A TranslationService version adapter" should {
    import TranslationService._

    "translate requests" in {
      // #snip_11-19
      val v1 = TestProbe()
      val v2 = system.actorOf(propsV2(v1.ref))
      val client = TestProbe()

      // initiate a request to the adapter
      v2 ! TranslateV2("Hur mår du?", "sv", "en", client.ref)

      // verify that the adapter asks the V1 service back-end
      val req1 = v1.expectMsgType[TranslateV1]
      req1.query should be("sv:en:Hur mår du?")

      // initiate a reply
      req1.replyTo ! "How are you?"

      // verify that the adapter transforms it correctly
      client.expectMsg(TranslationV2("Hur mår du?", "How are you?", "sv", "en"))

      // now verify translation errors
      v2 ! TranslateV2("Hur är läget?", "sv", "en", client.ref)
      val req2 = v1.expectMsgType[TranslateV1]
      // this implicitly verifies that no other communication happened with V1
      req2.query should be("sv:en:Hur är läget?")
      req2.replyTo ! "error:cannot parse input 'sv:en:Hur är läget?'"
      client.expectMsg(TranslationErrorV2("Hur är läget?", "sv", "en",
        "cannot parse input 'sv:en:Hur är läget?'"))

      v1.expectNoMessage(1.second)
      // #snip_11-19
    }

    "translate requests with async error reporting" in {
      // #snip_11-21
      val asyncErrors = TestProbe()
      val v1 = system.actorOf(mockV1props(asyncErrors.ref))
      val v2 = system.actorOf(propsV2(v1))
      val client = TestProbe()

      // initiate a request to the adapter
      v1 ! ExpectNominal
      v2 ! TranslateV2("Hur mår du?", "sv", "en", client.ref)

      // verify that the adapter transforms it correctly
      client.expectMsg(TranslationV2("Hur mår du?", "How are you?", "sv", "en"))

      // non-blocking check for async errors
      asyncErrors.expectNoMessage(0.seconds)

      // now verify translation errors
      v1 ! ExpectError
      v2 ! TranslateV2("Hur är läget?", "sv", "en", client.ref)
      client.expectMsg(TranslationErrorV2("Hur är läget?", "sv", "en",
        "cannot parse input 'sv:en:Hur är läget?'"))

      // final check for async errors
      asyncErrors.expectNoMessage(1.second)
      // #snip_11-21
    }

  }

}
