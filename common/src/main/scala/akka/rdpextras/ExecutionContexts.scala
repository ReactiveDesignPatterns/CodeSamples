package akka.rdpextras

import scala.concurrent.ExecutionContext

object ExecutionContexts {
  val sameThreadExecutionContext: ExecutionContext = akka.dispatch.ExecutionContexts.sameThreadExecutionContext
}
