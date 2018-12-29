/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

final case class Timestamp(time: Long) {
  def -(that: Timestamp): Timestamp = new Timestamp(time - that.time)
}

object Timestamp {

  implicit object TimestampOrdering extends Ordering[Timestamp] {
    override def compare(x: Timestamp, y: Timestamp): Int =
      x.time.compare(y.time)
  }

  implicit class TimestampOps(val timestamp: Timestamp) extends AnyVal {
    def toFiniteDuration: FiniteDuration = FiniteDuration(
      timestamp.time,
      TimeUnit.MILLISECONDS)

    def -(that: FiniteDuration): FiniteDuration = {
      FiniteDuration(timestamp.time - that.toMillis, TimeUnit.MILLISECONDS)
    }

    def >(that: FiniteDuration): Boolean = timestamp.time > that.toMillis

    def <(that: FiniteDuration): Boolean = timestamp.time < that.toMillis
  }

  def now: Timestamp = new Timestamp(System.currentTimeMillis())
}
