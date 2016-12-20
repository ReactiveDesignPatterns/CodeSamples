/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter17

import java.net.URI
import java.util.UUID
import akka.actor._
import scala.util.control.NonFatal

case class ManagerCommand(cmd: Command, id: Long, replyTo: ActorRef)
case class ManagerEvent(id: Long, event: Event)
case class ManagerQuery(cmd: Query, id: Long, replyTo: ActorRef)
case class ManagerResult(id: Long, result: Result)
case class ManagerRejection(id: Long, reason: String)

class Manager(var shoppingCart: ShoppingCart) extends Actor {
  /*
   * this is the usual constructor, the above allows priming with
   * previously persisted state.
   */
  def this() = this(ShoppingCart.empty)

  def receive = {
    case ManagerCommand(cmd, id, replyTo) =>
      try {
        val event = cmd match {
          case SetOwner(cart, owner) =>
            shoppingCart = shoppingCart.setOwner(owner)
            OwnerChanged(cart, owner)
          case AddItem(cart, item, count) =>
            shoppingCart = shoppingCart.addItem(item, count)
            ItemAdded(cart, item, count)
          case RemoveItem(cart, item, count) =>
            shoppingCart = shoppingCart.removeItem(item, count)
            ItemRemoved(cart, item, count)
        }
        replyTo ! ManagerEvent(id, event)
      } catch {
        case ex: IllegalArgumentException =>
          replyTo ! ManagerRejection(id, ex.getMessage)
      }
    case ManagerQuery(cmd, id, replyTo) =>
      try {
        val result = cmd match {
          case GetItems(cart) =>
            GetItemsResult(cart, shoppingCart.items)
        }
        replyTo ! ManagerResult(id, result)
      } catch {
        case ex: IllegalArgumentException =>
          replyTo ! ManagerRejection(id, ex.getMessage)
      }
  }
}

object ManagerExample extends App {
  def mkURI(): URI = URI.create(UUID.randomUUID().toString)

  val sys = ActorSystem("ObjectManager")

  val customer = CustomerRef(mkURI())
  val item1, item2 = ItemRef(mkURI())
  val shoppingCart = ShoppingCartRef(mkURI())

  val manager = sys.actorOf(Props(new Manager), "manager")

  sys.actorOf(Props(new Actor with ActorLogging {
    manager ! ManagerCommand(SetOwner(shoppingCart, customer), 0, self)
    manager ! ManagerCommand(AddItem(shoppingCart, item1, 5), 1, self)
    manager ! ManagerCommand(AddItem(shoppingCart, item1, -3), 2, self)
    manager ! ManagerCommand(AddItem(shoppingCart, item2, 6), 3, self)
    manager ! ManagerCommand(RemoveItem(shoppingCart, item1, 3), 4, self)
    manager ! ManagerQuery(GetItems(shoppingCart), 5, self)

    def receive = {
      case ManagerEvent(id, event)   => log.info("success ({}): {}", id, event)
      case ManagerRejection(id, msg) => log.warning("rejected ({}): {}", id, msg)
      case ManagerResult(id, result) =>
        log.info("result ({}): {}", id, result)
        context.system.terminate()
    }
  }), "client")
}
