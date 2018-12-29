/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15.pullable

import java.net.URL
import java.util.UUID

import akka.typed.ActorRef
import chapter15.SendEmailResult

// 代码清单 15-9
// Listing 15.9 Enabling the body to be pulled by the recipient

// #snip
final case class SendEmail(
  sender:        String,
  recipients:    List[String],
  bodyLocation:  URL,
  correlationID: UUID,
  replyTo:       ActorRef[SendEmailResult])

// #snip

