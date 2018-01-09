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

import java.util.concurrent.ForkJoinPool

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps

// 代码清单 3-12
// Listing 3.6 Aggregating a single result from two futures in Scala
class StagedFuturesForExample(inventoryService: InventoryService) {

  /**
   * Returns a Future of a tuple of the local inventory and overall
   * inventory for a specific product.  Both requests are sent
   * individually, but the Future to get the tuple of the values
   * cannot complete until both values are returned.
   *
   * The local inventory will just be a count, but the overall
   * inventory is a Map of Warehouse ID to count.
   */
  // #snip
  def getProductInventoryByPostalCode(
    productSku: Long,
    postalCode: String): Future[(Long, Map[String, Long])] = {
    // Import the duration DSL to be used in the timeout
    import scala.concurrent.duration._

    // Provide the thread pool and Future timeout value to be applied
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())
    implicit val timeout: FiniteDuration = 250 milliseconds

    // Define the futures so they can start doing their work
    val localInventoryFuture = Future {
      inventoryService.currentInventoryInWarehouse(
        productSku, postalCode)
    }
    val overallInventoryFutureByWarehouse = Future {
      inventoryService.currentInventoryOverallByWarehouse(
        productSku)
    }

    // Retrieve the values and return a future of the combined result
    for {
      local ← localInventoryFuture
      overall ← overallInventoryFutureByWarehouse
    } yield (local, overall)
  }
  // #snip
}
