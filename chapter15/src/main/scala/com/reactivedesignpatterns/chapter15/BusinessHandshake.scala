/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter15

import akka.actor._
import akka.persistence._
import scala.concurrent.duration._
import akka.event.LoggingReceive
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.scaladsl._
import akka.stream.ActorMaterializer
import akka.pattern.{ ask, pipe }
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import scala.concurrent.Await

object BusinessHandshake extends App {

  val config = ConfigFactory.parseString(
    """|akka.loglevel = DEBUG
       |akka.actor.debug {
       |  receive = on
       |  lifecycle = off
       |}""".stripMargin)

  val sys = ActorSystem("BusinessHandshake", config)
  val alice = sys.actorOf(Props[Alice], "alice")
  val bob = sys.deadLetters
  val sam = sys.actorOf(Props(new Sam(alice, bob, 1)), "sam")

  case class ChangeBudget(amount: BigDecimal, replyTo: ActorRef)
  case object ChangeBudgetDone
  case class CannotChangeBudget(reason: String)

  class Sam(alice: ActorRef, bob: ActorRef, amount: BigDecimal) extends Actor {
    def receive = talkToAlice()

    def talkToAlice(): Receive = {
      alice ! ChangeBudget(-amount, self)
      context.setReceiveTimeout(1.second)

      LoggingReceive {
        case ChangeBudgetDone           => context.become(talkToBob())
        case CannotChangeBudget(reason) => context.stop(self)
        case ReceiveTimeout             => alice ! ChangeBudget(-amount, self)
      }
    }

    def talkToBob(): Receive = {
      context.system.terminate()
      Actor.emptyBehavior
    }
  }

  class Alice extends Actor {
    var budget: BigDecimal = 10
    var alreadyDone: Set[ActorRef] = Set.empty

    def receive = LoggingReceive {
      case ChangeBudget(amount, replyTo) if alreadyDone(replyTo) =>
        replyTo ! ChangeBudgetDone
      case ChangeBudget(amount, replyTo) if amount + budget > 0 =>
        budget += amount
        alreadyDone += replyTo
        context.watch(replyTo)
        replyTo ! ChangeBudgetDone
      case ChangeBudget(_, replyTo) =>
        replyTo ! CannotChangeBudget("insufficient budget")
      case Terminated(saga) =>
        alreadyDone -= saga
    }
  }
}

object PersistentBusinessHandshake extends App {

  val config = ConfigFactory.parseString(
    """|akka.loglevel = DEBUG
       |akka.actor.debug {
       |  receive = on
       |  lifecycle = off
       |}
       |akka.persistence.journal {
       |  plugin = "akka.persistence.journal.leveldb"
       |  leveldb.native = off
       |}""".stripMargin)

  val sys = ActorSystem("BusinessHandshake", config)
  implicit val t = Timeout(3.seconds)

  val fake = sys.actorOf(Props(new FakeSam("Sam1")), "fakeSam")
  println(Await.result(fake ? "", 5.seconds))
  Thread.sleep(500)

  val alice = sys.actorOf(Props[PersistentAlice], "alice")
  val bob = sys.deadLetters
  val sam = sys.actorOf(Props(new PersistentSam(alice.path, bob.path, 1, "Sam1")), "sam")

  class FakeSam(override val persistenceId: String) extends PersistentActor {
    def receiveRecover = Actor.emptyBehavior
    def receiveCommand = {
      case _ =>
        deleteMessages(Long.MaxValue)
        context.become(waiting(sender()))
    }
    def waiting(replyTo: ActorRef): Receive = {
      case d @ (_: DeleteMessagesSuccess | _: DeleteMessagesFailure) =>
        replyTo ! d
        context.stop(self)
    }
  }

  case class ChangeBudget(amount: BigDecimal, replyTo: ActorRef, id: String)
  case object ChangeBudgetDone
  case class CannotChangeBudget(reason: String)

  case class AliceConfirmedChange(deliveryId: Long)
  case class AliceDeniedChange(deliveryId: Long)

  class PersistentSam(alice: ActorPath, bob: ActorPath, amount: BigDecimal, override val persistenceId: String)
      extends PersistentActor with AtLeastOnceDelivery with ActorLogging {

    def receiveCommand = Actor.emptyBehavior

    override def preStart(): Unit = {
      context.become(talkToAlice())
    }

    def receiveRecover = LoggingReceive {
      case AliceConfirmedChange(deliveryId) =>
        confirmDelivery(deliveryId)
        context.become(talkToBob())
      case AliceDeniedChange(deliveryId) =>
        confirmDelivery(deliveryId)
        context.stop(self)
    }

    def talkToAlice() = {
      log.debug("talking to Alice")
      var deliveryId: Long = 0
      deliver(alice)(id => { deliveryId = id; ChangeBudget(-amount, self, persistenceId) })

      LoggingReceive {
        case ChangeBudgetDone =>
          persist(AliceConfirmedChange(deliveryId)) { ev =>
            confirmDelivery(ev.deliveryId)
            context.become(talkToBob())
          }
        case CannotChangeBudget(reason) =>
          persist(AliceDeniedChange(deliveryId)) { ev =>
            confirmDelivery(ev.deliveryId)
            context.stop(self)
          }
      }
    }

    def talkToBob() = {
      context.system.terminate()
      Actor.emptyBehavior
    }
  }

  case class BudgetChanged(amount: BigDecimal, persistenceId: String)
  case object CleanupDoneList
  case class ChangeDone(persistenceId: String)

  class PersistentAlice extends PersistentActor with ActorLogging {
    def persistenceId: String = "Alice"

    implicit val mat = ActorMaterializer()
    import context.dispatcher

    var alreadyDone: Set[String] = Set.empty
    var budget: BigDecimal = 10

    val cleanupTimer = context.system.scheduler.schedule(1.hour, 1.hour, self, CleanupDoneList)

    def receiveCommand = LoggingReceive {
      case ChangeBudget(amount, replyTo, id) if alreadyDone(id) =>
        replyTo ! ChangeBudgetDone
      case ChangeBudget(amount, replyTo, id) if amount + budget > 0 =>
        persist(BudgetChanged(amount, id)) { ev =>
          budget += ev.amount
          alreadyDone += ev.persistenceId
          replyTo ! ChangeBudgetDone
        }
      case ChangeBudget(_, replyTo, _) =>
        replyTo ! CannotChangeBudget("insufficient budget")
      case CleanupDoneList =>
        val journal = PersistenceQuery(context.system).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
        for (persistenceId <- alreadyDone) {
          val stream = journal.currentEventsByPersistenceId(persistenceId).map(_.event).collect {
            case AliceConfirmedChange(_) => ChangeDone(persistenceId)
          }
          stream.runWith(Sink.head).pipeTo(self)
        }
      case ChangeDone(id) =>
        persist(ChangeDone(id)) { ev =>
          alreadyDone -= ev.persistenceId
        }
    }

    def receiveRecover = LoggingReceive {
      case BudgetChanged(amount, id) =>
        budget += amount
        alreadyDone += id
      case ChangeDone(id) =>
        alreadyDone -= id
    }

    override def postStop(): Unit = {
      cleanupTimer.cancel()
    }
  }

}
