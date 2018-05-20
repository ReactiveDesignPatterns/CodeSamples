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

import scala.concurrent.Future

object ParallelExecutionWithScalaFuture {

  class ReplyA
  class ReplyB
  class ReplyC
  class Result

  import scala.concurrent.ExecutionContext.Implicits.global

  def taskA(): Future[ReplyA] = Future(new ReplyA)
  def taskB(): Future[ReplyB] = Future(new ReplyB)
  def taskC(): Future[ReplyC] = Future(new ReplyC)

  def aggregate(a: ReplyA, b: ReplyB, c: ReplyC): Result = ???

  def main(args: Array[String]): Unit = {
    // #snip
    val fa: Future[ReplyA] = taskA()
    val fb: Future[ReplyB] = taskB()
    val fc: Future[ReplyC] = taskC()

    val fr: Future[Result] = for (a ← fa; b ← fb; c ← fc)
      yield aggregate(a, b, c)
    // #snip
    println(fr)
  }
}
