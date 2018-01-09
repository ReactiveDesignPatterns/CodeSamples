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

import akka.typed.ScalaDSL._
import akka.typed._

// 代码清单15-4
// Listing 15.4 Including the response explicitly in the request message

// #snip
object RequestResponseTypedActors {

  case class Request(msg: String, replyTo: ActorRef[Response])
  case class Response(msg: String)

  val responder: Behavior[Request] =
    Static {
      case Request(msg, replyTo) ⇒
        println(s"got request: $msg")
        replyTo ! Response("got it!")
    }

  def requester(responder: ActorRef[Request]): Behavior[Response] =
    SelfAware { self ⇒
      responder ! Request("hello", self)
      Total {
        case Response(msg) ⇒
          println(s"got response: $msg")
          Stopped
      }
    }

  def main(args: Array[String]): Unit = {
    ActorSystem("ReqResTyped", ContextAware[Unit] { ctx ⇒
      val res = ctx.spawn(responder, "responder")
      val req = ctx.watch(ctx.spawn(requester(res), "requester"))
      Full {
        case Sig(_, Terminated(`req`)) ⇒ Stopped
      }
    })
  }
}
// #snip
