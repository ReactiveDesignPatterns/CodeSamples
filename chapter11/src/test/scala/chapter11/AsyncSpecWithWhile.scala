/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import scala.concurrent.Future

// 代码清单11-4
class AsyncSpecWithWhile extends AsyncSpec {

  "Testing a purely async translation function with while" in {
    test()
  }

  override def assertFuture(future: Future[String], expected: String): Unit = {
    // #snip
    while (!future.isCompleted) Thread.sleep(50)
    // #snip
  }
}
