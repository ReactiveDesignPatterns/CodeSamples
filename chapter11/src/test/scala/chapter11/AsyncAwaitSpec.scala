/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import org.scalatest.{ Matchers, WordSpec }

import scala.async.Async.{ async, await }
import scala.concurrent.Future
import scala.concurrent.duration._

// 代码清单 11-14
class AsyncAwaitSpec extends WordSpec with Matchers {
  private implicit lazy val system: ActorSystem = ActorSystem()
  import system.dispatcher

  val tr = new TranslationService

  class TranslationWithTimeout(input: String) {

    def withTimeout(timeout: FiniteDuration): Future[String] = {
      val timeoutFuture = akka.pattern.after(
        timeout,
        system.scheduler)(Future.failed(new TimeoutException(s"Translation timeout")))
      val outputFuture = tr.translate(input)
      Future.firstCompletedOf(Seq(outputFuture, timeoutFuture))
    }
  }

  def translate(input: String): TranslationWithTimeout = new TranslationWithTimeout(input)

  "Using async and await to improve readability of asynchronous tests" in {
    // #snip
    async {
      val input = "Hur mår du?"
      val output = "How are you?"
      await(translate(input).withTimeout(5.seconds)) should be(output)
    }
    // #snip
  }
}
