package com.reactivedesignpatterns.chapter4

import akka.actor.Actor
import akka.actor.ActorRef

object EchoService {
  case class Request(tag: String, client: ActorRef)
  case class Response(tag: String)
}

class EchoService extends Actor {
  import EchoService._

  def receive = {
    case Request(tag, ref) => ref ! Response(tag)
  }
}