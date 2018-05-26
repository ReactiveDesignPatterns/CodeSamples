/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter17

import java.net.URI

// #snip
case class ItemRef(id: URI)

case class CustomerRef(id: URI)

case class ShoppingCartRef(id: URI)

// #snip

// #snip_17-1
case class ShoppingCart(
  items: Map[ItemRef, Int],
  owner: Option[CustomerRef]) {
  def setOwner(customer: CustomerRef): ShoppingCart = {
    require(owner.isEmpty, "owner cannot be overwritten")
    copy(owner = Some(customer))
  }

  def addItem(item: ItemRef, count: Int): ShoppingCart = {
    require(
      count > 0,
      s"count must be positive (trying to add $item with count $count)")
    val currentCount = items.getOrElse(item, 0)
    copy(items = items.updated(item, currentCount + count))
  }

  def removeItem(item: ItemRef, count: Int): ShoppingCart = {
    require(
      count > 0,
      s"count must be positive (trying to remove $item with count $count)")
    val currentCount = items.getOrElse(item, 0)
    val newCount = currentCount - count
    if (newCount <= 0)
      copy(items = items - item)
    else
      copy(items = items.updated(item, newCount))
  }

  // 代码清单 17-6
  def applyEvent(event: Event): ShoppingCart = event match {
    case OwnerChanged(_, owner)      ⇒ setOwner(owner)
    case ItemAdded(_, item, count)   ⇒ addItem(item, count)
    case ItemRemoved(_, item, count) ⇒ removeItem(item, count)
  }

  // 代码清单 17-6
}

object ShoppingCart {
  val empty = ShoppingCart(Map.empty, None)
}

// #snip_17-1

