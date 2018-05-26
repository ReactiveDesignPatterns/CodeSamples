/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03.future

trait InventoryService {
  def currentInventoryInWarehouse(productSku: Long, postalCode: String): Long

  def currentInventoryOverallByWarehouse(productSku: Long): Map[String, Long]
}
