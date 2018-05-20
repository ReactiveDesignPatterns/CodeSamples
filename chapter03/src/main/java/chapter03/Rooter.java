/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

package chapter03;

// 代码清单3-5
// Referential transparency: allowing substitution of precomputed values

// #snip
public class Rooter {
  private final double value;
  private Double root = null;

  public Rooter(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  public double getRoot() {
    if (root == null) {
      root = Math.sqrt(value);
    }
    return root;
  }
}
// #snip
