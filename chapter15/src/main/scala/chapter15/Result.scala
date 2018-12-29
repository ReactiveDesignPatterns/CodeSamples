/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15

import java.util.UUID

sealed trait Result

case object ReceiveTimeout extends Result

// 代码清单 15-7
// Listing 15.7 Encapsulated information needed for multiple SMTP exchanges

// #snip
final case class SendEmailResult(
  correlationID: UUID,
  status:        StatusCode,
  explanation:   Option[String]) extends Result

// #snip

