/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter12

sealed trait StorageStatus

object StorageStatus {
  case object Failed extends StorageStatus
  case object Unknown extends StorageStatus
  case object Gated extends StorageStatus
}
