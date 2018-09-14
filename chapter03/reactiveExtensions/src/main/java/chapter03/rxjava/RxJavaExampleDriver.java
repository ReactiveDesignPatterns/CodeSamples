/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

// 代码清单3-15
// #snip
package chapter03.rxjava;

public class RxJavaExampleDriver {
  private static final RxJavaExample rxJavaExample = new RxJavaExample();

  public static void main(String[] args) {
    String[] strings = {"a", "b", "c"};
    rxJavaExample.observe(strings);
  }
}
// #snip
