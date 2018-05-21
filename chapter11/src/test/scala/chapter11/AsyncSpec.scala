/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.Future

// 代码清单11-2
abstract class AsyncSpec extends WordSpec with Matchers {
  val tr = new TranslationService

  def translate(input: String): Future[String] = {
    tr.translate(input)
  }

  def test(): Unit = {
    // #snip
    val input = "Hur mår du?"
    val output = "How are you?"
    val future = translate(input)
    // what now?
    // #snip
    assertFuture(future, output)
  }

  def assertFuture(future: Future[String], expected: String)
}
