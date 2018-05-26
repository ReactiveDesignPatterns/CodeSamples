/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.Await

// 代码清单11-1
// Listing 11.1 Testing a purely synchronous translation function
class SynchronousSpec extends WordSpec with Matchers {
  val tr = new TranslationService

  def translate(input: String): String = {
    import scala.concurrent.duration._
    Await.result(tr.translate(input), 2.seconds)
  }

  " Testing a purely synchronous translation function" in {
    // #snip
    val input = "Hur mår du?"
    val output = "How are you?"
    translate(input) should be(output)
    // #snip
  }
}
