package com.reactivedesignpatterns.chapter4

import org.scalatest._
import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import akka.testkit.TestProbe

class StepParentSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem()

  "An actor that throws an exception" must {
    "Result in the supervisor returning a reference to that actor" in {
      val testProbe = TestProbe()
			val parent = system.actorOf(Props[StepParent], "stepParent")
			parent.tell(Props[MyStepParentActor], testProbe.ref)
			val child = testProbe.expectMsgType[ActorRef]
    }
  }

  override def afterAll(): Unit = {
    system.shutdown()
  }
}

class StepParent extends Actor {
   override val supervisorStrategy = OneForOneStrategy() {
     case thr => Restart
   }
   def receive = {
     case p: Props => 
       sender ! context.actorOf(p, "child")
   }
}

class MyStepParentActor extends Actor {
  def receive = {
    case _ => throw new NullPointerException
  }
}
