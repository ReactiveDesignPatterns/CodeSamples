/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
