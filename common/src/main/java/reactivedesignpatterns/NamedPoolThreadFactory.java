/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package reactivedesignpatterns;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A named thread factory
 *
 * @author kerr
 */
public class NamedPoolThreadFactory implements ThreadFactory {
  private final AtomicInteger counter = new AtomicInteger(0);
  private final String prefix;
  private final boolean makeDaemons;
  private final ThreadGroup group;

  public NamedPoolThreadFactory(final String prefix, final boolean makeDaemons) {
    this.prefix = prefix;
    this.makeDaemons = makeDaemons;
    this.group = new ThreadGroup(Thread.currentThread().getThreadGroup(), prefix);
  }

  @Override
  public Thread newThread(final Runnable runnable) {
    Objects.requireNonNull(runnable, "Parameter runnable should not be null.");
    Thread thread = new Thread(group, runnable, "" + prefix + "-" + counter.incrementAndGet());
    thread.setDaemon(makeDaemons);
    if (thread.getPriority() != Thread.NORM_PRIORITY) {
      thread.setPriority(Thread.NORM_PRIORITY);
    }
    return thread;
  }
}
