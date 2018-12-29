/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15

import akka.typed.ScalaDSL._
import akka.typed._

// 代码清单15-4
// Listing 15.4 Including the response explicitly in the request message

// #snip
object RequestResponseTypedActors {

  final case class Request(msg: String, replyTo: ActorRef[Response])

  final case class Response(msg: String)

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
