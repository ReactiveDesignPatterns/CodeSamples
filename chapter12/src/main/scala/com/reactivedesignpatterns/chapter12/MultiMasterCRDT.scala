package com.reactivedesignpatterns.chapter12

class MultiMasterCRDT {

  import akka.actor._
  
  case class Greet(whom: String)
  
  class Greeter extends Actor {
    def receive = {
      case Greet(whom) =>
        sender() ! s"Hello $whom!"
        val delegate = context.actorOf(grumpyProps)
        context.become(grumpy(delegate))
    }
    def grumpy(delegate: ActorRef): Receive = {
      case g: Greet => delegate forward g
    }
  }
  
  val grumpyProps = Props(new Actor {
    def receive = {
      case Greet(whom) =>
        sender() ! s"Go away, $whom!"
    }
  })
  
}