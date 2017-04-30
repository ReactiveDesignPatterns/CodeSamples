package com.reactivedesignpatterns.chapter4

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.actor.Actor
import akka.util.Timeout
import com.reactivedesignpatterns.Defaults._
import java.util.concurrent.TimeoutException
import akka.actor.Props

class TranslationService {
  import ExecutionContext.Implicits.global

  def translate(input: String): Future[String] = Future { Thread.sleep(100); "How are you?" }

  def translate(input: String, ec: ExecutionContext): Future[String] =
    Future { Thread.sleep(100); "How are you?" }(ec)
}

/**
 * Another implementation that is based on Actors; this is used in the example
 * which shows how to assert the absence of indirectly invoked messages by way
 * of a protocol adapter test for this translation service.
 */
object TranslationService {

  /**
   * Simplistic version 1 of the protocol: the reply will just be a String.
   */
  case class TranslateV1(query: String, replyTo: ActorRef)

  /**
   * Implementation of the TranslateV1 protocol.
   */
  private class TranslatorV1 extends Actor {
    def receive = {
      case TranslateV1(query, replyTo) =>
        if (query == "sv:en:Hur mår du?") {
          replyTo ! "How are you?"
        } else {
          replyTo ! s"error:cannot translate '$query'"
        }
    }
  }
  
  def propsV1: Props = Props(new TranslatorV1)

  /**
   * More advanced version 2 of the protocol with proper reply types.
   * Languages are communicated as Strings for brevity, in a real project
   * these would be modeled as a proper Language type (statically known
   * enumeration or based on runtime registration of values).
   */
  case class TranslateV2(phrase: String, inputLanguage: String, outputLanguage: String, replyTo: ActorRef)

  sealed trait TranslationResponseV2
  case class TranslationV2(inputPhrase: String, outputPhrase: String, inputLanguage: String, outputLanguage: String)
  case class TranslationErrorV2(inputPhrase: String, inputLanguage: String, outputLanguage: String, errorMessage: String)

  /**
   * Implementation of the TranslateV2 protocol based on TranslatorV1.
   */
  private class TranslatorV2(v1: ActorRef) extends Actor {
    implicit val timeout = Timeout(5.seconds)
    import context.dispatcher

    def receive = {
      case TranslateV2(phrase, in, out, replyTo) =>
        v1 ? (TranslateV1(s"$in:$out:$phrase", _)) collect {
          case str: String =>
            if (str.startsWith("error:")) {
              TranslationErrorV2(phrase, in, out, str.substring(6))
            } else {
              TranslationV2(phrase, str, in, out)
            }
        } recover {
          case _: TimeoutException =>
            TranslationErrorV2(phrase, in, out, "timeout while talking to V1 back-end")
        } pipeTo replyTo
    }
  }
  
  def propsV2(v1: ActorRef): Props = Props(new TranslatorV2(v1))
}
