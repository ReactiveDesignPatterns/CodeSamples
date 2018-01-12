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

package chapter11

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

case class Timestamp(time: Long) {
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
