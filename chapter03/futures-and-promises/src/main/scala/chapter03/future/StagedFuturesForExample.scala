/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
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
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(
    ForkJoinPool.commonPool())

  def getProductInventoryByPostalCode(
    productSku: Long,
    postalCode: String): Future[(Long, Map[String, Long])] = {
    // Import the duration DSL to be used in the timeout
    import scala.concurrent.duration._

    // Provide the thread pool and Future timeout value to be applied
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
