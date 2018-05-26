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

class ActorSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  private implicit lazy val system: ActorSystem = ActorSystem()

  // 代码清单11-3
  "Testing a actor based translation function" in {
    val translationService = system.actorOf(TranslationService.props)
    // #snip
    val input = "Hur mår du?"
    val output = "How are you?"
    val probe = TestProbe()
    translationService ! Translate(input, probe.ref)
    // when can we continue?
    // #snip
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}
