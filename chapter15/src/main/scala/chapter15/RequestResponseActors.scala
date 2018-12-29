/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15

import akka.actor._

// 代码清单 15-3
// Listing 15.3 Untyped Akka Actors modeling request–response

// #snip
object RequestResponseActors {

  final case class Request(msg: String)

  final case class Response(msg: String)

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
