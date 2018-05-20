/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
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
