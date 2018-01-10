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

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import akka.testkit.TestProbe
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class FailureParentSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem()

  "Using a FailureParent" must {
    "Result in failures being collected and returned" in {
      val failures = TestProbe()
      val failureParent = system.actorOf(Props(new FailureParent(failures.ref)))
      failureParent ! TestFailureParentMessage
      failures.expectMsgType[NullPointerException]
    }
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}

case object TestFailureParentMessage

class FailureParent(failures: ActorRef) extends Actor {
  val props: Props = Props[MyFailureParentActor]
  val child: ActorRef = context.actorOf(props, "child")
  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case f ⇒ failures ! f; Stop
  }
  def receive: PartialFunction[Any, Unit] = {
    case msg ⇒ child forward msg
  }
}

class MyFailureParentActor extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case _ ⇒ throw new NullPointerException
  }
}
