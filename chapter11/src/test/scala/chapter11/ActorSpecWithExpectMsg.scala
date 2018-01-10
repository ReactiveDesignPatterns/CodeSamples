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
