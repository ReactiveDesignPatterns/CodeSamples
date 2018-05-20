/*
 * Copyright 2018 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
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

// 代码清单3-16
// Listing 3.7 An Actor example in Akka

// #snip
package chapter03.actor

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import akka.event.LoggingReceive

case object Start
case class CounterMessage(counterValue: Int)
case class CounterTooLargeException(message: String) extends Exception(message)

class SupervisorActor extends Actor with ActorLogging {
  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _: CounterTooLargeException ⇒ Restart
  }

  private val actor2 = context.actorOf(Props[SecondActor], "second-actor")
  private val actor1 = context.actorOf(Props(new FirstActor(actor2)), "first-actor")

  def receive: PartialFunction[Any, Unit] = {
    case Start ⇒ actor1 ! Start
  }
}

class AbstractCounterActor extends Actor with ActorLogging {
  protected var counterValue = 0

  def receive: PartialFunction[Any, Unit] = {
    case _ ⇒
  }

  def counterReceive: Receive = LoggingReceive {
    case CounterMessage(i) if i <= 1000 ⇒
      counterValue = i
      log.info(s"Counter value: $counterValue")
      sender ! CounterMessage(counterValue + 1)
    case CounterMessage(_) ⇒
      throw CounterTooLargeException(
        "Exceeded max value of counter!")
  }

  override def postRestart(reason: Throwable): Unit = {
    context.parent ! Start
  }
}

class FirstActor(secondActor: ActorRef) extends AbstractCounterActor {
  override def receive = LoggingReceive {
    case Start ⇒
      context.become(counterReceive)
      log.info("Starting counter passing.")
      secondActor ! CounterMessage(counterValue + 1)
  }
}

class SecondActor() extends AbstractCounterActor {
  override def receive: Receive = counterReceive
}

object Example extends App {
  val system = ActorSystem("counter-supervision-example")
  val supervisor = system.actorOf(Props[SupervisorActor])
  supervisor ! Start
}
// #snip
