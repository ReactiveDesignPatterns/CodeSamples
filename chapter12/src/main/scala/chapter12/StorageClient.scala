/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter12

import scala.concurrent.Future

// 代码清单 12-3
// Listing 12.3 Circuit breaker: limiting requests from a client
class StorageClient {
  import scala.concurrent.duration._
  // #snip
  private val limiter = new RateLimiter(100, 2.seconds)

  def persistForThisClient(job: Job): Future[StorageStatus] = {
    import akka.rdpextras.ExecutionContexts.sameThreadExecutionContext
    limiter
      .call(persist(job))
      .recover {
        case RateLimitExceeded ⇒ StorageStatus.Failed
      }
  }
  // #snip
  def persist(job: Job): Future[StorageStatus] = ??? // remote call
}
