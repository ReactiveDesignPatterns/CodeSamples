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

package chapter12

import akka.actor.ActorSystem
import akka.pattern.{ CircuitBreaker, CircuitBreakerOpenException }

import scala.concurrent.Future

// 代码清单 12-4
// Listing 12.4 Gating a client
class GatedStorageClient(system: ActorSystem) {
  import scala.concurrent.duration._
  // #snip
  private val limiter = new RateLimiter(100, 2.seconds)
  private val breaker = CircuitBreaker(
    system.scheduler,
    10, Duration.Zero, 10.seconds)

  def persistForThisClient(job: Job): Future[StorageStatus] = {
    import akka.rdpextras.ExecutionContexts.sameThreadExecutionContext
    breaker
      .withCircuitBreaker(limiter.call(persist(job)))
      .recover {
        case RateLimitExceeded              ⇒ StorageStatus.Failed
        case _: CircuitBreakerOpenException ⇒ StorageStatus.Gated
      }
  }
  // #snip
  def persist(job: Job): Future[StorageStatus] = ??? // remote call
}
