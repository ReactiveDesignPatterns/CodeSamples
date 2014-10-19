package scala.concurrent.rdpextras

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object SynchronousEventLoop extends ExecutionContext {
  override def execute(r: Runnable): Unit = Future.InternalCallbackExecutor.execute(r)
  override def reportFailure(t: Throwable): Unit = Future.InternalCallbackExecutor.reportFailure(t)
}