package com.reactivedesignpatterns.chapter13

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.Deadline
import scala.concurrent.Future

case object RateLimitExceeded extends RuntimeException

class RateLimiter(requests: Int, period: FiniteDuration) {
  private val startTimes = {
    val onePeriodAgo = Deadline.now - period
    Array.fill(requests)(onePeriodAgo)
  }
  private var position = 0
  private def lastTime = startTimes(position)
  private def enqueue(time: Deadline) = {
    startTimes(position) = time
    position += 1
    if (position == requests) position = 0
  }
  def call[T](block: => Future[T]): Future[T] = {
    val now = Deadline.now
    if ((now - lastTime) < period) Future.failed(RateLimitExceeded)
    else {
      enqueue(now)
      block
    }
  }
}

object CircuitBreaker {
  private object StorageFailed extends RuntimeException
  private def sendToStorage(job: Job): Future[StorageStatus] = {
    // make an asynchronous request to the storage subsystem
    val f: Future[StorageStatus] = ???
    // map storage failures to Future failures to alert the breaker
    f.map {
      case StorageStatus.Failed => throw StorageFailed
      case other                => other
    }
  }
  private val breaker = CircuitBreaker(
    system.scheduler, // used for scheduling timeouts
    5, // number of failures in a row when it trips
    300.millis, // timeout for each service call
    30.seconds // time before trying to close after tripping
    )
  def persist(job: Job): Future[StorageStatus] =
    breaker
      .withCircuitBreaker(sendToStorage(job))
      .recover {
        case StorageFailed                  => StorageStatus.Failed
        case _: TimeoutException            => StorageStatus.Unknown
        case _: CircuitBreakerOpenException => StorageStatus.Failed
      }
}
