/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter17

//#snip_17-2
trait ShoppingCartMessage {
  def shoppingCart: ShoppingCartRef
}

sealed trait Command extends ShoppingCartMessage

final case class SetOwner(
  shoppingCart: ShoppingCartRef,
  owner:        CustomerRef) extends Command

final case class AddItem(
  shoppingCart: ShoppingCartRef,
  item:         ItemRef,
  count:        Int) extends Command

final case class RemoveItem(
  shoppingCart: ShoppingCartRef,
  item:         ItemRef,
  count:        Int) extends Command

sealed trait Query extends ShoppingCartMessage

final case class GetItems(shoppingCart: ShoppingCartRef) extends Query

sealed trait Event extends ShoppingCartMessage

final case class OwnerChanged(
  shoppingCart: ShoppingCartRef,
  owner:        CustomerRef) extends Event

final case class ItemAdded(
  shoppingCart: ShoppingCartRef,
  item:         ItemRef,
  count:        Int) extends Event

final case class ItemRemoved(
  shoppingCart: ShoppingCartRef,
  item:         ItemRef,
  count:        Int) extends Event

sealed trait Result extends ShoppingCartMessage

final case class GetItemsResult(
  shoppingCart: ShoppingCartRef,
  items:        Map[ItemRef, Int]) extends Result

//#snip_17-2
