/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chapter15.pattern.ask

import java.util.UUID

import akka.typed.ActorRef
import chapter15.StatusCode

sealed trait MyCommands
private case class MyEmailResult(correlationID: UUID, status: StatusCode, explanation: Option[String]) extends MyCommands

case class StartVerificationProcess(userEmail: String, replyTo: ActorRef[VerificationProcessResponse]) extends MyCommands

sealed trait VerificationProcessResponse
case class VerificationProcessStarted(userEmail: String) extends VerificationProcessResponse
case class VerificationProcessFailed(userEmail: String) extends VerificationProcessResponse

