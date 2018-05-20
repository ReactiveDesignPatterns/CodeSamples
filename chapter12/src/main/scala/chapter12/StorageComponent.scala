/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/ 
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
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
