/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter17

trait ShoppingCartMessage {
  def shoppingCart: ShoppingCartRef
}

sealed trait Command extends ShoppingCartMessage
case class SetOwner(shoppingCart: ShoppingCartRef, owner: CustomerRef) extends Command
case class AddItem(shoppingCart: ShoppingCartRef, item: ItemRef, count: Int) extends Command
case class RemoveItem(shoppingCart: ShoppingCartRef, item: ItemRef, count: Int) extends Command

sealed trait Query extends ShoppingCartMessage
case class GetItems(shoppingCart: ShoppingCartRef) extends Query
