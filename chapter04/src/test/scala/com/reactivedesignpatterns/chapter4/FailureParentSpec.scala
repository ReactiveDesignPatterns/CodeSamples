package com.reactivedesignpatterns.chapter4

import org.scalatest._
import akka.actor._
import akka.actor.SupervisorStrategy.Stop
import akka.testkit.TestProbe

class FailureParentSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem()

  "Using a FailureParent" must {
	  "Result in failures being collected and returned" in {
		  val failures = TestProbe()
			val failureParent = system.actorOf(Props(new FailureParent(failures.ref)))
			failureParent ! TestFailureParentMessage
			failures.expectMsgType[NullPointerException]
	  }
	}

  override def afterAll(): Unit = {
    system.shutdown()
  }
}

case object TestFailureParentMessage 

class FailureParent(failures: ActorRef) extends Actor {
	val props = Props[MyFailureParentActor]
  val child = context.actorOf(props, "child")
  override val supervisorStrategy = OneForOneStrategy() {
    case f => failures ! f; Stop
  }
  def receive = {
    case msg => child forward msg
  }
}

class MyFailureParentActor extends Actor {
  def receive = {
    case _ => throw new NullPointerException
  }
}
