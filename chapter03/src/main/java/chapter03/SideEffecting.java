/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03;

// 代码清单3-6
// Limiting usability with side effects

import java.io.Serializable;

// #snip
public class SideEffecting implements Serializable, Cloneable {
  private int count;

  public SideEffecting(int start) {
    this.count = start;
  }

  public int next() {
    this.count += Math.incrementExact(this.count);
    return this.count;
  }
}
// #snip
