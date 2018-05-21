/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import scala.concurrent.Future

// 代码清单11-5
class AsyncSpecWithWhileLoopIterationsBounded extends AsyncSpec {

  "Testing a purely async translation function with while loop iterations bounded" in {
    test()
  }

  override def assertFuture(future: Future[String], expected: String): Unit = {
    // #snip
    var i = 20
    while (!future.isCompleted && i > 0) {
      i -= 1
      Thread.sleep(50)
    }
    if (i == 0) fail("translation was not received in time")
    // #snip
  }
}
