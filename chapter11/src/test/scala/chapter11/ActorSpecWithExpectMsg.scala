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
import scala.concurrent.duration.Duration

// 代码清单11-7
// Listing 11.3 Expecting replies with a TestProbe
class ActorSpecWithExpectMsg extends WordSpec with Matchers with BeforeAndAfterAll {
  private implicit lazy val system: ActorSystem = ActorSystem()

  "Expecting replies with a TestProbe" in {
    val translationService = system.actorOf(TranslationService.props)
    import scala.concurrent.duration._
    // #snip
    val input = "Hur mår du?"
    val output = "How are you?"
    val probe = TestProbe()
    translationService ! Translate(input, probe.ref)
    probe.expectMsg(1.second, output)
    // #snip
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}
