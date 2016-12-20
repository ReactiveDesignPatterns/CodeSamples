/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter17

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.persistence.journal._
import akka.persistence.query._
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import com.typesafe.config.ConfigFactory
import java.net.URI
import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.duration._

class ShoppingCartTagging(system: ExtendedActorSystem) extends WriteEventAdapter {
  def manifest(event: Any): String = ""

  def toJournal(event: Any): Any =
    event match {
      case s: ShoppingCartMessage => Tagged(event, Set("shoppingCart"))
      case other                  => other
    }
}

class ShoppingCartSimulator extends Actor with ActorLogging {
  def rnd = ThreadLocalRandom.current

  val items = Array(
    "apple",
    "banana",
    "plum",
    "pear",
    "peach").map(f => ItemRef(new URI(f)))
  def pickItem() = items(rnd.nextInt(items.length))

  val customers = Array(
    "alice",
    "bob",
    "charlie",
    "mallory").map(c => CustomerRef(new URI(c)))
  def pickCustomer() = customers(rnd.nextInt(customers.length))

  val id = Iterator from 0
  def command(cmd: Command) = ManagerCommand(cmd, id.next, self)

  def driveCart(num: Int) = {
    val cartRef = ShoppingCartRef(new URI(f"cart$num%08X"))
    val manager = context.actorOf(Props(new PersistentObjectManager), cartRef.id.toString)
    manager ! command(SetOwner(cartRef, pickCustomer()))
    while (rnd.nextDouble() < 0.95) {
      val cmd =
        if (rnd.nextBoolean()) AddItem(cartRef, pickItem(), rnd.nextInt(14) + 1)
        else RemoveItem(cartRef, pickItem(), rnd.nextInt(10) + 1)
      manager ! command(cmd)
    }
    manager ! ManagerQuery(GetItems(cartRef), num, self)
  }

  case class Cont(id: Int)
  self ! Cont(0)

  def receive = {
    case Cont(n)             => driveCart(n)
    case ManagerEvent(id, _) => if (id % 10000 == 0) log.info("done {} commands", id)
    case ManagerResult(num, GetItemsResult(cart, items)) =>
      context.stop(context.child(cart.id.toString).get)
      self ! Cont(num.toInt + 1)
  }
}

case class GetTopProducts(id: Long, replyTo: ActorRef)
case class TopProducts(id: Long, products: Map[ItemRef, Int])

object TopProductListener {
  private class IntHolder(var value: Int)
}

class TopProductListener extends Actor with ActorLogging {
  import TopProductListener._
  implicit val materializer = ActorMaterializer()

  val readJournal =
    PersistenceQuery(context.system)
      .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

  readJournal.eventsByTag("shoppingCart", 0)
    .collect { case EventEnvelope(_, _, _, add: ItemAdded) => add }
    .groupedWithin(100000, 1.second)
    .addAttributes(Attributes.asyncBoundary)
    .runForeach { seq: Seq[ItemAdded] =>
      val histogram = seq.foldLeft(Map.empty[ItemRef, IntHolder]) {
        (map, event) =>
          map.get(event.item) match {
            case Some(holder) => { holder.value += event.count; map }
            case None         => map.updated(event.item, new IntHolder(event.count))
          }
      }
      self ! TopProducts(0, histogram.map(p => (p._1, p._2.value)))
    }

  var topProducts = Map.empty[ItemRef, Int]

  def receive = {
    case GetTopProducts(id, replyTo) => replyTo ! TopProducts(id, topProducts)
    case TopProducts(_, products) =>
      topProducts = products
      log.info("new {}", products)
  }
}

object EventStreamExample extends App {
  val config = ConfigFactory.parseString("""
akka.loglevel = INFO
akka.actor.debug.unhandled = on
akka.actor.warn-about-java-serializer-usage = off
akka.persistence.snapshot-store.plugin = "akka.persistence.no-snapshot-store"
akka.persistence.journal {
  plugin = "akka.persistence.journal.leveldb"
  leveldb {
    native = off
    event-adapters {
      tagging = "com.reactivedesignpatterns.chapter17.ShoppingCartTagging"
    }
    event-adapter-bindings {
      "com.reactivedesignpatterns.chapter17.ShoppingCartMessage" = tagging
    }
  }
}
""")
  val sys = ActorSystem("EventStream", config)
  sys.actorOf(Props(new ShoppingCartSimulator), "simulator")
  sys.actorOf(Props(new TopProductListener), "listener")
}
