/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter17

import java.net.URI
import java.util.UUID

import akka.actor._
import akka.persistence.PersistentActor
import com.typesafe.config.ConfigFactory

// #snip_17-7
class PersistentObjectManager extends PersistentActor {
  // we expect the name to be the shopping card ID
  override def persistenceId: String = context.self.path.name

  var shoppingCart: ShoppingCart = ShoppingCart.empty

  def receiveCommand: Receive = {
    case ManagerCommand(cmd, id, replyTo) ⇒
      try {
        val event = cmd match {
          case SetOwner(cart, owner)         ⇒ OwnerChanged(cart, owner)
          case AddItem(cart, item, count)    ⇒ ItemAdded(cart, item, count)
          case RemoveItem(cart, item, count) ⇒ ItemRemoved(cart, item, count)
        }
        // perform the update here in order to treat validation errors immediately
        shoppingCart = shoppingCart.applyEvent(event)
        persist(event) { _ ⇒
          replyTo ! ManagerEvent(id, event)
        }
      } catch {
        case ex: IllegalArgumentException ⇒
          replyTo ! ManagerRejection(id, ex.getMessage)
      }
    case ManagerQuery(cmd, id, replyTo) ⇒
      try {
        val result = cmd match {
          case GetItems(cart) ⇒ GetItemsResult(cart, shoppingCart.items)
        }
        replyTo ! ManagerResult(id, result)
      } catch {
        case ex: IllegalArgumentException ⇒
          replyTo ! ManagerRejection(id, ex.getMessage)
      }
  }

  def receiveRecover: Receive = {
    case e: Event ⇒ shoppingCart = shoppingCart.applyEvent(e)
  }
}

// #snip_17-7

/*
 * Running the application for the first time will do the same as the ManagerExample.
 * Running it again will read the persisted state from the local working directory
 * and consequently reject the SetOwner command, plus it will list four entries in the
 * GetItemsResult.
 */
object PersistentObjectManagerExample extends App {
  def mkURI(): URI = URI.create(UUID.randomUUID().toString)

  val config = ConfigFactory.parseString(
    """
akka.actor.warn-about-java-serializer-usage = off
akka.persistence.journal {
  plugin = "akka.persistence.journal.leveldb"
  leveldb.native=off
}
akka.persistence.snapshot-store.plugin = "akka.persistence.no-snapshot-store"
""")
  val sys = ActorSystem("ObjectManager", config)

  val customer = CustomerRef(mkURI())
  val item1, item2 = ItemRef(mkURI())
  val shoppingCart = ShoppingCartRef(new URI("myCart"))

  val manager = sys.actorOf(Props(new PersistentObjectManager), shoppingCart.id.toString)

  sys.actorOf(Props(new Actor with ActorLogging {
    manager ! ManagerCommand(SetOwner(shoppingCart, customer), 0, self)
    manager ! ManagerCommand(AddItem(shoppingCart, item1, 5), 1, self)
    manager ! ManagerCommand(AddItem(shoppingCart, item1, -3), 2, self)
    manager ! ManagerCommand(AddItem(shoppingCart, item2, 6), 3, self)
    manager ! ManagerCommand(RemoveItem(shoppingCart, item1, 3), 4, self)
    manager ! ManagerQuery(GetItems(shoppingCart), 5, self)

    def receive: Receive = {
      case ManagerEvent(id, event)   ⇒ log.info("success ({}): {}", id, event)
      case ManagerRejection(id, msg) ⇒ log.warning("rejected ({}): {}", id, msg)
      case ManagerResult(id, result) ⇒
        log.info("result ({}): {}", id, result)
        context.system.terminate()
    }
  }), "client")
}
