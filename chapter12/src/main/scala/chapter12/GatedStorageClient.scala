/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
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
