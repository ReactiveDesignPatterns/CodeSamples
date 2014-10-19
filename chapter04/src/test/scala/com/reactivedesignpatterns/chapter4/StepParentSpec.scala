package com.reactivedesignpatterns.chapter4

import org.scalatest._
import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import akka.testkit.TestProbe

class StepParentSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  "An actor that throws an exception" must {
    "Result in the supervisor returning a reference to that actor" in {
      implicit val system = ActorSystem()
      val testProbe = TestProbe()
			val parent = system.actorOf(Props[StepParent], "stepParent")
			parent.tell(Props[MyActor], testProbe.ref)
			val child = testProbe.expectMsgType[ActorRef]
    }
  }
}

case object TestMessage

class StepParent extends Actor {
   override val supervisorStrategy = OneForOneStrategy() {
     case thr => Restart
   }
   def receive = {
     case p: Props => 
       sender ! context.actorOf(p, "child")
   }
}

class MyActor extends Actor {
  def receive = {
    case _ => throw new NullPointerException
  }
}
