/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter15

import akka.typed._
import akka.typed.ScalaDSL._
import akka.typed.AskPattern._
import java.util.UUID
import akka.event.Logging
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.AskTimeoutException

object AskPattern {

  sealed trait MyCommands
  case class StartVerificationProcess(userEmail: String, replyTo: ActorRef[VerificationProcessResponse]) extends MyCommands
  private case class MyEmailResult(correlationID: UUID, status: StatusCode, explanation: String) extends MyCommands

  sealed trait VerificationProcessResponse
  case class VerificationProcessStarted(userEmail: String) extends VerificationProcessResponse
  case class VerificationProcessFailed(userEmail: String) extends VerificationProcessResponse

  case class SendEmail(sender: String, recipients: List[String],
                       body: String, correlationID: UUID,
                       replyTo: ActorRef[SendEmailResult])

  case class SendEmailResult(correlationID: UUID, status: StatusCode,
                             explanation: String)

  sealed trait StatusCode
  object StatusCode {
    case object OK extends StatusCode
    case object Failed extends StatusCode
  }

  def withoutAskPattern(emailGateway: ActorRef[SendEmail]): Behavior[StartVerificationProcess] =
    ContextAware[MyCommands] { ctx =>
      val log = Logging(ctx.system.eventStream, "VerificationProcessManager")
      var statusMap = Map.empty[UUID, (String, ActorRef[VerificationProcessResponse])]
      val adapter = ctx.spawnAdapter((s: SendEmailResult) => MyEmailResult(s.correlationID, s.status, s.explanation))

      Static {
        case StartVerificationProcess(userEmail, replyTo) =>
          val corrID = UUID.randomUUID()
          val request = SendEmail("verification@example.com", List(userEmail), constructBody(userEmail, corrID), corrID, adapter)
          emailGateway ! request
          statusMap += corrID -> (userEmail, replyTo)
          ctx.schedule(5.seconds, ctx.self, MyEmailResult(corrID, StatusCode.Failed, "timeout"))
        case MyEmailResult(corrID, status, expl) =>
          statusMap.get(corrID) match {
            case None =>
              log.error("received SendEmailResult for unknown correlation ID {}", corrID)
            case Some((userEmail, replyTo)) =>
              status match {
                case StatusCode.OK =>
                  log.debug("successfully started the verification process for {}", userEmail)
                  replyTo ! VerificationProcessStarted(userEmail)
                case StatusCode.Failed =>
                  log.info("failed to start the verification process for {}: {}", userEmail, expl)
                  replyTo ! VerificationProcessFailed(userEmail)
              }
              statusMap -= corrID
          }
      }
    }.narrow[StartVerificationProcess]

  def withChildActor(emailGateway: ActorRef[SendEmail]): Behavior[StartVerificationProcess] =
    ContextAware { ctx: ActorContext[StartVerificationProcess] =>
      val log = Logging(ctx.system.eventStream, "VerificationProcessManager")

      Static {
        case StartVerificationProcess(userEmail, replyTo) =>
          val corrID = UUID.randomUUID()
          val childActor = ctx.spawnAnonymous(Props(FullTotal[SendEmailResult] {
            case Sig(ctx, PreStart) =>
              ctx.setReceiveTimeout(5.seconds)
              Same
            case Sig(_, ReceiveTimeout) =>
              log.warning("verification process initiation timed out for {}", userEmail)
              replyTo ! VerificationProcessFailed(userEmail)
              Stopped
            case Msg(_, SendEmailResult(`corrID`, StatusCode.OK, _)) =>
              log.debug("successfully started the verification process for {}", userEmail)
              replyTo ! VerificationProcessStarted(userEmail)
              Stopped
            case Msg(_, SendEmailResult(`corrID`, StatusCode.Failed, explanation)) =>
              log.info("failed to start the verification process for {}: {}", userEmail, explanation)
              replyTo ! VerificationProcessFailed(userEmail)
              Stopped
            case Msg(_, SendEmailResult(wrongID, _, _)) =>
              log.error("received wrong SendEmailResult for corrID {}", corrID)
              Same
          }))
          val request = SendEmail("verification@example.com", List(userEmail), constructBody(userEmail, corrID), corrID, childActor)
          emailGateway ! request
      }
    }

  def withAskPattern(emailGateway: ActorRef[SendEmail]): Behavior[StartVerificationProcess] =
    ContextAware { ctx =>
      val log = Logging(ctx.system.eventStream, "VerificationProcessManager")
      implicit val timeout = Timeout(5.seconds)
      import ctx.executionContext

      Static {
        case StartVerificationProcess(userEmail, replyTo) =>
          val corrID = UUID.randomUUID()
          val response: Future[SendEmailResult] =
            emailGateway ? (SendEmail("verification@example.com", List(userEmail), constructBody(userEmail, corrID), corrID, _))
          response.map {
            case SendEmailResult(`corrID`, StatusCode.OK, _) =>
              log.debug("successfully started the verification process for {}", userEmail)
              VerificationProcessStarted(userEmail)
            case SendEmailResult(`corrID`, StatusCode.Failed, explanation) =>
              log.info("failed to start the verification process for {}: {}", userEmail, explanation)
              VerificationProcessFailed(userEmail)
            case SendEmailResult(wrongID, _, _) =>
              log.error("received wrong SendEmailResult for corrID {}", corrID)
              VerificationProcessFailed(userEmail)
          }.recover {
            case _: AskTimeoutException =>
              log.warning("verification process initiation timed out for {}", userEmail)
              VerificationProcessFailed(userEmail)
          }.foreach(result => replyTo ! result)
      }
    }

  private def constructBody(userEmail: String, corrID: UUID): String = ???

}
