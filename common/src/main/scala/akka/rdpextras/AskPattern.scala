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

package akka.rdpextras

import akka.actor._
import akka.pattern._
import scala.concurrent.Future
import akka.util.Timeout

object AskPattern {

  def ask(actorRef: ActorRef, timeout: Timeout, f: ActorRef ⇒ Any): Future[Any] = actorRef match {
    case ref: InternalActorRef if ref.isTerminated ⇒
      actorRef ! f(null)
      Future.failed[Any](new AskTimeoutException(s"Recipient[$actorRef] had already been terminated."))
    case ref: InternalActorRef ⇒
      if (timeout.duration.length <= 0)
        Future.failed[Any](new IllegalArgumentException(s"Timeout length must not be negative, question not sent to [$actorRef]"))
      else {
        val a = PromiseActorRef(ref.provider, timeout, targetName = actorRef.toString, "unknown")
        actorRef.tell(f(a), a)
        a.result.future
      }
    case _ ⇒ Future.failed[Any](new IllegalArgumentException(s"Unsupported recipient ActorRef type, question not sent to [$actorRef]"))
  }

}
