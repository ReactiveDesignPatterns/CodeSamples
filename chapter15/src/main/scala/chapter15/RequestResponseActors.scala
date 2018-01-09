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

package chapter15

import akka.actor._

// 代码清单 15-3
// Listing 15.3 Untyped Akka Actors modeling request–response

// #snip
object RequestResponseActors {

  case class Request(msg: String)
  case class Response(msg: String)

  class Responder extends Actor {
    def receive: Receive = {
      case Request(msg) ⇒
        println(s"got request: $msg")
        sender() ! Response("got it!")
    }
  }

  class Requester(responder: ActorRef) extends Actor {
    responder ! Request("hello")

    def receive: Receive = {
      case Response(msg) ⇒
        println(s"got response: $msg")
        context.system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("ReqRes")
    val responder = sys.actorOf(Props[Responder], "responder")
    val requester = sys.actorOf(Props(new Requester(responder)), "requester")
  }

}
// #snip
