/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package com.reactivedesignpatterns

import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Success

object Defaults {

  val Timestamp: Deadline.type = Deadline
  type Timestamp = Deadline

  implicit class AskableActorRef(val ref: ActorRef) extends AnyVal {
    def ?(f: ActorRef ⇒ Any)(implicit timeout: Timeout): Future[Any] = akka.rdpextras.AskPattern.ask(ref, timeout, f)
  }

  implicit class PipeTo[T](val f: Future[T]) extends AnyVal {
    def pipeTo(ref: ActorRef)(implicit self: ActorRef = ActorRef.noSender): Future[T] =
      f.andThen { case Success(msg) ⇒ ref ! msg }(SynchronousEventLoop)
  }

}
