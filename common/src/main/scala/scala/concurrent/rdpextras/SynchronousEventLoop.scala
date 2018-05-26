/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package scala.concurrent.rdpextras

import scala.concurrent.{ ExecutionContext, Future }

object SynchronousEventLoop extends ExecutionContext {
  override def execute(r: Runnable): Unit = Future.InternalCallbackExecutor.execute(r)

  override def reportFailure(t: Throwable): Unit = Future.InternalCallbackExecutor.reportFailure(t)
}
