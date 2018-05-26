/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15.streamed

import java.util.UUID

import akka.typed.ActorRef
import chapter15.SendEmailResult

// 代码清单15-8
// Listing 15.8 Separating the body so it can be delivered on demand

// #snip
case class SendEmail(
  sender:        String,
  recipients:    List[String],
  correlationID: UUID,
  replyTo:       ActorRef[SendEmailResult])(body: Source[String]) extends StreamedRequest {
  override def payload: Source[String] = body
}

// #snip
