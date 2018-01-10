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

package chapter12

import akka.actor.ActorSystem
import akka.pattern.{CircuitBreaker, CircuitBreakerOpenException}

import scala.concurrent.{Future, TimeoutException}

// 代码清单 12-1
// Listing 12.1 Using a circuit breaker to give a failed component time to recover
class StorageComponent(system:ActorSystem) {
  // #snip
  private object StorageFailed extends RuntimeException

  private def sendToStorage(job: Job): Future[StorageStatus] = {
    import akka.rdpextras.ExecutionContexts.sameThreadExecutionContext
    val f: Future[StorageStatus] = ???
    f.map {
      case StorageStatus.Failed => throw StorageFailed
      case other => other
    }
  }

  import scala.concurrent.duration._
  private val breaker = CircuitBreaker(
    system.scheduler,
    5,
    300.millis,
    30.seconds,
  )

  def persist(job: Job): Future[StorageStatus] = {
    import akka.rdpextras.ExecutionContexts.sameThreadExecutionContext
    breaker
      .withCircuitBreaker(sendToStorage(job))
      .recover {
        case StorageFailed => StorageStatus.Failed
        case _: TimeoutException => StorageStatus.Unknown
        case _: CircuitBreakerOpenException => StorageStatus.Failed
      }
  }
  // #snip
}
