/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter12

// 代码清单 12-1
// Listing 12.2 Protecting a component by using a rate limiter

// #snip
import scala.concurrent.Future
import scala.concurrent.duration.{ Deadline, FiniteDuration }

case object RateLimitExceeded extends RuntimeException

class RateLimiter(requests: Int, period: FiniteDuration) {
  private val startTimes = {
    val onePeriodAgo = Deadline.now - period
    Array.fill(requests)(onePeriodAgo)
  }
  private var position = 0

  private def lastTime = startTimes(position)

  private def enqueue(time: Deadline): Unit = {
    startTimes(position) = time
    position += 1
    if (position == requests) position = 0
  }

  def call[T](block: ⇒ Future[T]): Future[T] = {
    val now = Deadline.now
    if ((now - lastTime) < period) {
      Future.failed(RateLimitExceeded)
    } else {
      enqueue(now)
      block
    }
  }
}

// #snip
