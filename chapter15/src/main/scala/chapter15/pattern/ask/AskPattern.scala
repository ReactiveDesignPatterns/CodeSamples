/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15.pattern.ask

import java.util.UUID

import akka.actor.Scheduler
import akka.pattern.AskTimeoutException
import akka.typed.AskPattern._
import akka.typed.ScalaDSL._
import akka.typed._
import akka.util.Timeout
import chapter15._

import scala.concurrent.Future
import scala.concurrent.duration._

object AskPattern {
  // #snip_15-13
  def withoutAskPattern(emailGateway: ActorRef[SendEmail]): Behavior[StartVerificationProcess] =
    ContextAware[MyCommands] { ctx ⇒
      val log = new BusLogging(
        ctx.system.eventStream,
        "VerificationProcessManager", getClass, ctx.system.logFilter)
      var statusMap = Map.empty[UUID, (String, ActorRef[VerificationProcessResponse])]
      val adapter = ctx.spawnAdapter((s: SendEmailResult) ⇒
        MyEmailResult(s.correlationID, s.status, s.explanation))

      Static {
        case StartVerificationProcess(userEmail, replyTo) ⇒
          val corrID = UUID.randomUUID()
          val request = SendEmail(
            "verification@example.com",
            List(userEmail),
            constructBody(userEmail, corrID), corrID, adapter)
          emailGateway ! request
          statusMap += corrID -> (userEmail, replyTo)
          ctx.schedule(5.seconds, ctx.self,
            MyEmailResult(corrID, StatusCode.Failed, Some("timeout")))
        case MyEmailResult(corrID, status, expl) ⇒
          statusMap.get(corrID) match {
            case None ⇒
              log.error(
                "received SendEmailResult for unknown correlation ID {}",
                corrID)
            case Some((userEmail, replyTo)) ⇒
              status match {
                case StatusCode.OK ⇒
                  log.debug(
                    "successfully started the verification process for {}",
                    userEmail)
                  replyTo ! VerificationProcessStarted(userEmail)
                case StatusCode.Failed ⇒
                  log.info(
                    "failed to start the verification process for {}: {}",
                    userEmail, expl)
                  replyTo ! VerificationProcessFailed(userEmail)
              }
              statusMap -= corrID
          }
      }
    }.narrow[StartVerificationProcess]
  // #snip_15-13

  // #snip_15-11
  def withChildActor(emailGateway: ActorRef[SendEmail]): Behavior[StartVerificationProcess] =
    ContextAware { ctx: ActorContext[StartVerificationProcess] ⇒
      val log = new BusLogging(
        ctx.system.eventStream,
        "VerificationProcessManager", getClass, ctx.system.logFilter)

      Static {
        case StartVerificationProcess(userEmail, replyTo) ⇒
          val corrID = UUID.randomUUID()
          val childActor = ctx.spawnAnonymous(FullTotal[Result] {
            case Sig(ctx, PreStart) ⇒
              ctx.setReceiveTimeout(5.seconds, ReceiveTimeout)
              Same
            case Msg(_, ReceiveTimeout) ⇒
              log.warning(
                "verification process initiation timed out for {}",
                userEmail)
              replyTo ! VerificationProcessFailed(userEmail)
              Stopped
            case Msg(_, SendEmailResult(`corrID`, StatusCode.OK, _)) ⇒
              log.debug(
                "successfully started the verification process for {}",
                userEmail)
              replyTo ! VerificationProcessStarted(userEmail)
              Stopped
            case Msg(_, SendEmailResult(`corrID`, StatusCode.Failed, explanation)) ⇒
              log.info(
                "failed to start the verification process for {}: {}",
                userEmail, explanation)
              replyTo ! VerificationProcessFailed(userEmail)
              Stopped
            case Msg(_, SendEmailResult(wrongID, _, _)) ⇒
              log.error(
                "received wrong SendEmailResult for corrID {}",
                corrID)
              Same
          })
          val request = SendEmail(
            "verification@example.com",
            List(userEmail),
            constructBody(userEmail, corrID), corrID, childActor)
          emailGateway ! request
      }
    }
  // #snip_15-11

  // #snip_15-12
  def withAskPattern(emailGateway: ActorRef[SendEmail]): Behavior[StartVerificationProcess] =
    ContextAware { ctx ⇒
      val log = new BusLogging(
        ctx.system.eventStream,
        "VerificationProcessManager", getClass, ctx.system.logFilter)
      implicit val timeout: Timeout = Timeout(5.seconds)
      import ctx.executionContext
      implicit val scheduler: Scheduler = ctx.system.scheduler

      Static {
        case StartVerificationProcess(userEmail, replyTo) ⇒
          val corrID = UUID.randomUUID()
          val response: Future[SendEmailResult] =
            emailGateway ? (SendEmail(
              "verification@example.com",
              List(userEmail),
              constructBody(userEmail, corrID), corrID, _))
          response.map {
            case SendEmailResult(`corrID`, StatusCode.OK, _) ⇒
              log.debug(
                "successfully started the verification process for {}",
                userEmail)
              VerificationProcessStarted(userEmail)
            case SendEmailResult(`corrID`, StatusCode.Failed, explanation) ⇒
              log.info(
                "failed to start the verification process for {}: {}",
                userEmail, explanation)
              VerificationProcessFailed(userEmail)
            case SendEmailResult(wrongID, _, _) ⇒
              log.error(
                "received wrong SendEmailResult for corrID {}",
                corrID)
              VerificationProcessFailed(userEmail)
          }.recover {
            case _: AskTimeoutException ⇒
              log.warning(
                "verification process initiation timed out for {}",
                userEmail)
              VerificationProcessFailed(userEmail)
          }.foreach(result ⇒ replyTo ! result)
      }
    }
  // #snip_15-12

  private def constructBody(userEmail: String, corrID: UUID): String = ???

}
