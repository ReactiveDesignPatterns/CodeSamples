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
