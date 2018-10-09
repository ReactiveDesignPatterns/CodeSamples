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

// 代码清单 3-13
// Listing 3.6 Aggregating a single result from two futures in Scala
class StagedFuturesAsyncExample(inventoryService: InventoryService) {

  /**
   * Returns a Future of a tuple of the local inventory and overall
   * inventory for a specific product.  Both requests are sent
   * individually, but the Future to get the tuple of the values
   * cannot complete until both values are returned.
   *
   * The local inventory will just be a count, but the overall
   * inventory is a Map of Warehouse ID to count.
   */
  def getProductInventoryByPostalCode(
    productSku: Long,
    postalCode: String): Future[(Long, Map[String, Long])] = {
    // Import the duration DSL to be used in the timeout

    // Provide the thread pool to be applied
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())

    // Retrieve the values and return a future of the combined result
    // #snip
    import scala.async.Async.{ async, await }
    val resultFuture = async {
      val localInventoryFuture = async {
        inventoryService.currentInventoryInWarehouse(productSku, postalCode)
      }
      val overallInventoryFutureByWarehouse = async {
        inventoryService.currentInventoryOverallByWarehouse(productSku)
      }
      (await(localInventoryFuture), await(overallInventoryFutureByWarehouse))
    }
    // #snip
    resultFuture
  }
}
