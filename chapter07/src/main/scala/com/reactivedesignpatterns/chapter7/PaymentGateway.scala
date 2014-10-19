package com.reactivedesignpatterns.chapter7

import akka.actor.ActorRef

object PaymentGateway {

  object V1 {
    case class CreditCard(name: String, number: String, expiry: String)
    case class Amount(printed: String)
    
    case class Authorize(card: CreditCard, amount: Amount, replyTo: ActorRef)
    
    trait AuthorizeResult {
      def isSuccessful: Boolean
      def authorization: String
      def failureMessage: String
    }
    
    case class Capture(authorization: String, amount: Amount, replyTo: ActorRef)
    
    trait CaptureResult {
      def isSuccessful: Boolean
      def authorization: String
      def failureMessage: String
    }
    
    case class Void(authorization: String, replyTo: ActorRef)
    
    trait VoidResult {
      def isSuccessful: Boolean
      def failureMessage: String
    }
    
    case class Refund(card: CreditCard, amount: Amount, replyTo: ActorRef)
    
  }
  
  object V2 {
    case class CreditCard(name: String, number: String, expiry: String)
    case class Amount(printed: String)
    
    case class Authorize(card: CreditCard, amount: Amount, replyTo: ActorRef)
    
    sealed trait AuthorizeResult
    case class Authorized(authorization: String)
    case class NotAuthorized(message: String)
    
    case class Capture(authorization: String, amount: Amount, replyTo: ActorRef)
    
    sealed trait CaptureResult
    case class Captured(authorization: String, amount: Amount)
    case class NotCaptured(authorization: String, message: String)
    
    case class Void(authorization: String, replyTo: ActorRef)
    
  }
  
}