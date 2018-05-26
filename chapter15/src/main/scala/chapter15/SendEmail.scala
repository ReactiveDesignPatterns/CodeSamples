/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15

import java.util.UUID

import akka.typed.ActorRef

// 代码清单 15-7
// Listing 15.7 Encapsulated information needed for multiple SMTP exchanges

// #snip
case class SendEmail(
  sender:        String,
  recipients:    List[String],
  body:          String,
  correlationID: UUID,
  replyTo:       ActorRef[SendEmailResult])

// #snip
