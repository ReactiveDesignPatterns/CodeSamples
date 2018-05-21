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
