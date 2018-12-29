/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter07

import akka.actor.ActorRef

object PaymentGateway {

  object V1 {

    final case class CreditCard(name: String, number: String, expiry: String)

    final case class Amount(printed: String)

    final case class Authorize(card: CreditCard, amount: Amount, replyTo: ActorRef)

    trait AuthorizeResult {
      def isSuccessful: Boolean

      def authorization: String

      def failureMessage: String
    }

    final case class Capture(authorization: String, amount: Amount, replyTo: ActorRef)

    trait CaptureResult {
      def isSuccessful: Boolean

      def authorization: String

      def failureMessage: String
    }

    final case class Void(authorization: String, replyTo: ActorRef)

    trait VoidResult {
      def isSuccessful: Boolean

      def failureMessage: String
    }

    final case class Refund(card: CreditCard, amount: Amount, replyTo: ActorRef)

  }

  object V2 {

    final case class CreditCard(name: String, number: String, expiry: String)

    final case class Amount(printed: String)

    final case class Authorize(card: CreditCard, amount: Amount, replyTo: ActorRef)

    sealed trait AuthorizeResult

    final case class Authorized(authorization: String)

    final case class NotAuthorized(message: String)

    final case class Capture(authorization: String, amount: Amount, replyTo: ActorRef)

    sealed trait CaptureResult

    final case class Captured(authorization: String, amount: Amount)

    final case class NotCaptured(authorization: String, message: String)

    final case class Void(authorization: String, replyTo: ActorRef)

  }

}
