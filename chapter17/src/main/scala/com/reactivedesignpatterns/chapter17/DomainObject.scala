/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter17

import java.net.URI

case class ItemRef(id: URI)
case class CustomerRef(id: URI)
case class ShoppingCartRef(id: URI)

case class ShoppingCart(items: Map[ItemRef, Int], owner: Option[CustomerRef]) {
  def setOwner(customer: CustomerRef): ShoppingCart = {
    require(owner.isEmpty, "owner cannot be overwritten")
    copy(owner = Some(customer))
  }

  def addItem(item: ItemRef, count: Int): ShoppingCart = {
    require(count > 0, s"count must be positive (trying to add $item with count $count)")
    val currentCount = items.get(item).getOrElse(0)
    copy(items = items.updated(item, currentCount + count))
  }

  def removeItem(item: ItemRef, count: Int): ShoppingCart = {
    require(count > 0, s"count must be positive (trying to remove $item with count $count)")
    val currentCount = items.get(item).getOrElse(0)
    val newCount = currentCount - count
    if (newCount <= 0) copy(items = items - item)
    else copy(items = items.updated(item, newCount))
  }

  // This is here for section 16.3
  def applyEvent(event: Event): ShoppingCart = event match {
    case OwnerChanged(_, owner)      => setOwner(owner)
    case ItemAdded(_, item, count)   => addItem(item, count)
    case ItemRemoved(_, item, count) => removeItem(item, count)
  }
}

object ShoppingCart {
  val empty = ShoppingCart(Map.empty, None)
}
