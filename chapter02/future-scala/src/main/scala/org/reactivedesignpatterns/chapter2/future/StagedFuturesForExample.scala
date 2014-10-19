package org.reactivedesignpatterns.chapter2.future

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.ForkJoinPool

trait InventoryService {
  def currentInventoryInWarehouse(productSku: Long, postalCode: String): Long
  def currentInventoryOverallByWarehouse(productSku: Long): Map[String, Long]
}

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
  def getProductInventoryByPostalCode(productSku: Long, postalCode: String): Future[(Long, Map[String, Long])] = {
    // Import the duration DSL to be used in the timeout
    import scala.concurrent.duration._

    // Provide the thread pool and Future timeout value to be applied
    implicit val ec = ExecutionContext.fromExecutor(new ForkJoinPool())
    implicit val timeout = 250 milliseconds

    // Define the futures so they can start doing their work
    val localInventoryFuture = Future {
      inventoryService.currentInventoryInWarehouse(productSku, postalCode)
    }
    val overallInventoryFutureByWarehouse = Future {
      inventoryService.currentInventoryOverallByWarehouse(productSku)
    }

    // Retrieve the values and return a future of the combined result
    for {
      local <- localInventoryFuture
      overall <- overallInventoryFutureByWarehouse
    } yield (local, overall)
  }
}