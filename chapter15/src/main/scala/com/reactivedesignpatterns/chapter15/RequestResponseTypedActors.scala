/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter15

import akka.typed._
import akka.typed.ScalaDSL._
import com.typesafe.config.ConfigFactory

object RequestResponseTypedActors {

  case class Request(msg: String, replyTo: ActorRef[Response])
  case class Response(msg: String)

  val responder: Behavior[Request] =
    Static {
      case Request(msg, replyTo) =>
        println(s"got request: $msg")
        replyTo ! Response("got it!")
    }

  def requester(responder: ActorRef[Request]): Behavior[Response] =
    SelfAware { self =>
      responder ! Request("hello", self)
      Total {
        case Response(msg) =>
          println(s"got response: $msg")
          Stopped
      }
    }

  def main(args: Array[String]): Unit = {
    ActorSystem("ReqResTyped", Props(ContextAware[Unit] { ctx =>
      val res = ctx.spawn(Props(responder), "responder")
      val req = ctx.watch(ctx.spawn(Props(requester(res)), "requester"))
      Full {
        case Sig(ctx, Terminated(`req`)) => Stopped
      }
    }))
  }
}