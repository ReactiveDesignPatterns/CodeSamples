/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
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
