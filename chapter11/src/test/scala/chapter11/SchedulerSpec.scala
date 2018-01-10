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

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.testkit.TestProbe
import chapter11.SchedulerSpec.{ Schedule, Scheduler }
import org.scalatest.{ BeforeAndAfterAll, FlatSpec }

import scala.concurrent.Await
import scala.concurrent.duration.{ Duration, FiniteDuration }

// 代码清单11-8
// Listing 11.4 Using a TestProbe to receive the response from the scheduler
class SchedulerSpec extends FlatSpec with BeforeAndAfterAll {
  private implicit lazy val system: ActorSystem = ActorSystem()

  "Using a TestProbe to receive the response from the scheduler" should "ok" in {

    import scala.concurrent.duration._
    val scheduler = system.actorOf(Scheduler.props)
    // #snip
    val probe = TestProbe()
    val start = Timestamp.now
    scheduler ! Schedule(probe.ref, "tick", 1.second)
    probe.expectMsg(2.seconds, "tick")
    val stop = Timestamp.now
    val duration = stop - start
    assert(duration > 950.millis, "tick came in early")
    assert(duration < 1050.millis, "tick came in late")
    // when can we continue?
    // #snip
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}

object SchedulerSpec {
  sealed trait SchedulerCommand
  case class Schedule(replyTo: ActorRef, msg: Any, delay: FiniteDuration) extends SchedulerCommand

  class Scheduler extends Actor {
    override def receive: Receive = {
      case Schedule(replyTo, msg, delay) ⇒
        import context.dispatcher
        context.system.scheduler
          .scheduleOnce(delay, replyTo, msg)
    }
  }

  object Scheduler {
    def props: Props = Props[Scheduler]
  }

}
