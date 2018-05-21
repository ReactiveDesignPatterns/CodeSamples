/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import chapter11.TranslationService.Translate
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.Await
import scala.concurrent.duration._

class ActorSpecWithCallingThreadDispatcher extends WordSpec with Matchers with BeforeAndAfterAll {
  private implicit lazy val system: ActorSystem = ActorSystem()

  // Listing 11.11 Processing messages on the calling thread with CallingThreadDispatcher
  // 代码清单11-11
  "Processing messages on the calling thread with CallingThreadDispatcher" in {
    // #snip
    val translationService = system.actorOf(
      TranslationService.props.withDispatcher("akka.test.calling-thread-dispatcher"))
    val input = "Hur mår du?"
    val output = "How are you?"
    val probe = TestProbe()
    translationService ! Translate(input, probe.ref)
    probe.expectMsg(0.seconds, output)
    // #snip
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}
