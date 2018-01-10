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

package chapter03.future

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor }
import java.util.concurrent.ForkJoinPool

@RunWith(classOf[JUnit4])
class StagedFuturesForExampleTest {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool())
  implicit val timeout: FiniteDuration = 250 milliseconds

  @Test
  def testInventoryCount: Unit = {
    val stagedFutures = new StagedFuturesForExample(new InventoryService() {
      def currentInventoryInWarehouse(productSku: Long, postalCode: String): Long = {
        5
      }
      def currentInventoryOverallByWarehouse(productSku: Long): Map[String, Long] = {
        Map("212" -> 407L, "312" -> 81L, "412" -> 6L)
      }
    })

    val results = stagedFutures.getProductInventoryByPostalCode(1234L, "212")
    results.foreach {
      case (local, overall) ⇒
        org.junit.Assert.assertEquals(local, 5)
        org.junit.Assert.assertEquals(overall("212"), 407L)
    }
  }
}
