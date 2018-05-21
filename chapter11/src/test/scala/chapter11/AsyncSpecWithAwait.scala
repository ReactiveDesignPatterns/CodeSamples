/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.{ Await, Future }

// 代码清单11-6
// Listing 11.2 Awaiting the result blocks synchronously on the translation
class AsyncSpecWithAwait extends WordSpec with Matchers {
  val tr = new TranslationService

  def translate(input: String): Future[String] = {
    tr.translate(input)
  }

  "Awaiting the result blocks synchronously on the translation" in {
    import scala.concurrent.duration._
    // #snip
    val input = "Hur mår du?"
    val output = "How are you?"
    val result = Await.result(translate(input), 1.second)
    result should be(output)
    // #snip
  }

}
