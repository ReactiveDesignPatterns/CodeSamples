/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import java.util.UUID

import akka.actor.{ Actor, ActorRef, ActorSystem, Cancellable, Props }
import akka.testkit.TestProbe
import chapter11.SchedulerSpec._
import org.scalatest.{ BeforeAndAfterAll, FlatSpec }

import scala.concurrent.Await
import scala.concurrent.duration.{ Duration, FiniteDuration }

class SchedulerSpec extends FlatSpec with BeforeAndAfterAll {
  private implicit lazy val system: ActorSystem = ActorSystem()

  // 代码清单11-8
  // Listing 11.4 Using a TestProbe to receive the response from the scheduler

  "Using a TestProbe to receive the response from the scheduler" should "ok" in {

    import scala.concurrent.duration._
    val scheduler = system.actorOf(Scheduler.props)
    // #snip
    val probe = TestProbe()

    val start = Timestamp.now
    scheduler ! Schedule(probe.ref, "tick", 1.second)
    probe.expectMsg(2.seconds, "tick")

    val stop = Timestamp.now

    val duration = stop - start
    assert(duration > 950.millis, "tick came in early")
    assert(duration < 1050.millis, "tick came in late")
    // when can we continue?
    // #snip
  }

  // Listing 11.8 Verifying that no additional messages are received
  it should "Verifying that no additional messages are received" in {
    // #snip_11-8
    import scala.concurrent.duration._
    val scheduler = system.actorOf(Scheduler.props)

    val probe = TestProbe()
    scheduler ! ScheduleRepeatedly(probe.ref, 1.second, "tick")
    val token = probe.expectMsgType[SchedulerToken]
    probe.expectMsg(1500.millis, "tick")
    scheduler ! CancelSchedule(token, probe.ref)
    probe.expectMsg(100.millis, ScheduleCanceled)
    probe.expectNoMessage(2.seconds)
    // #snip_11-8
  }

  override def afterAll(): Unit = {
    val terminated = system.terminate()
    Await.ready(terminated, Duration.Inf)
  }
}

object SchedulerSpec {

  sealed trait SchedulerCommand

  final case class Schedule(replyTo: ActorRef, msg: Any, delay: FiniteDuration) extends SchedulerCommand

  final case class ScheduleRepeatedly(replyTo: ActorRef, delay: FiniteDuration, msg: String) extends SchedulerCommand

  final case class CancelSchedule(token: SchedulerToken, replyTo: ActorRef) extends SchedulerCommand

  sealed trait SchedulerEvent

  final case class SchedulerToken(token: String) extends SchedulerEvent

  final case object ScheduleCanceled extends SchedulerEvent

  final case class ScheduleNotFound(token: SchedulerToken) extends SchedulerEvent

  class Scheduler extends Actor {
    private var schedulerTokens = Map.empty[SchedulerToken, Cancellable]

    override def receive: Receive = {
      case Schedule(replyTo, msg, delay) ⇒
        import context.dispatcher
        context.system.scheduler
          .scheduleOnce(delay, replyTo, msg)
      case ScheduleRepeatedly(replyTo, delay, msg) ⇒
        import context.dispatcher
        val cancellable = context.system.scheduler
          .schedule(
            initialDelay = delay,
            interval = delay,
            receiver = replyTo,
            message = msg
          )
        val schedulerToken = SchedulerToken(UUID.randomUUID().toString)
        schedulerTokens = schedulerTokens.updated(schedulerToken, cancellable)
        replyTo ! schedulerToken
      case CancelSchedule(token, replyTo) ⇒
        schedulerTokens.get(token) match {
          case Some(cancellable) ⇒
            cancellable.cancel()
            replyTo ! ScheduleCanceled
          case None ⇒
            replyTo ! ScheduleNotFound(token)
        }
    }
  }

  object Scheduler {
    def props: Props = Props[Scheduler]
  }

}
