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

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.testkit.TestProbe
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StepParentSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem()

  "An actor that throws an exception" must {
    "Result in the supervisor returning a reference to that actor" in {
      val testProbe = TestProbe()
      val parent = system.actorOf(Props[StepParent], "stepParent")
      parent.tell(Props[MyStepParentActor], testProbe.ref)
      val child = testProbe.expectMsgType[ActorRef]
    }
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}

class StepParent extends Actor {
  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case thr ⇒ Restart
  }
  def receive: PartialFunction[Any, Unit] = {
    case p: Props ⇒
      sender ! context.actorOf(p, "child")
  }
}

class MyStepParentActor extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case _ ⇒ throw new NullPointerException
  }
}
