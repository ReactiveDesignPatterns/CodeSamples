/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package akka.rdpextras

import scala.concurrent.ExecutionContext

object ExecutionContexts {
  implicit val sameThreadExecutionContext: ExecutionContext = akka.dispatch.ExecutionContexts.sameThreadExecutionContext
}
