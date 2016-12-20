/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter17

sealed trait Event extends ShoppingCartMessage
case class OwnerChanged(shoppingCart: ShoppingCartRef, owner: CustomerRef) extends Event
case class ItemAdded(shoppingCart: ShoppingCartRef, item: ItemRef, count: Int) extends Event
case class ItemRemoved(shoppingCart: ShoppingCartRef, item: ItemRef, count: Int) extends Event

sealed trait Result extends ShoppingCartMessage
case class GetItemsResult(shoppingCart: ShoppingCartRef, items: Map[ItemRef, Int]) extends Result
