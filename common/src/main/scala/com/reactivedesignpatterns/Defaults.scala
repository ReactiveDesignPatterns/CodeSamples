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

package com.reactivedesignpatterns

import scala.concurrent.duration._
import akka.actor.ActorRef

import scala.concurrent.Future
import akka.util.Timeout

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
