/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import akka.testkit.TestProbe
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

// #snip_11-26
case object TestFailureParentMessage

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

// #snip_11-26

// #snip_11-25
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

// #snip_11-25

class MyFailureParentActor extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case _ ⇒ throw new NullPointerException
  }
}
