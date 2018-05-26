/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.testkit.TestProbe
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

// #snip_11-24
class StepParentSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem()

  "An actor that throws an exception" must {
    "Result in the supervisor returning a reference to that actor" in {
      val testProbe = TestProbe()
      val parent = system.actorOf(Props[StepParent], "stepParent")
      parent.tell(Props[MyActor], testProbe.ref)
      val child = testProbe.expectMsgType[ActorRef]
      // ...
      // Test whatever we want in the actor
    }
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}

// #snip_11-24

// #snip_11-23
class StepParent extends Actor {
  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case thr ⇒ Restart
  }

  def receive: Receive = {
    case p: Props ⇒
      sender ! context.actorOf(p, "child")
  }
}
// #snip_11-23

// #snip_11-22
class MyActor extends Actor {
  def receive: Receive = {
    case _ ⇒ throw new NullPointerException
  }
}
// #snip_11-22
