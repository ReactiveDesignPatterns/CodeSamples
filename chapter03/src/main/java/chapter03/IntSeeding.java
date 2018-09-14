/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadLocalRandom;

// 代码清单3-7
public class IntSeeding {
  private static final Logger logger = LoggerFactory.getLogger(IntSeeding.class);

  public class IntRandom {
    public int next() {
      return ThreadLocalRandom.current().nextInt();
    }
  }

  private final IntRandom se = new IntRandom();

  public int nextInt() {
    // #snip
    final int next = se.next();
    if (logger.isDebugEnabled()) {
      logger.debug("Next is " + se.next());
    }
    return next;
    // #snip
  }
}
