package akka.rdpextras

import scala.concurrent.ExecutionContext

object ExecutionContexts {
  implicit val sameThreadExecutionContext: ExecutionContext = akka.dispatch.ExecutionContexts.sameThreadExecutionContext
}
