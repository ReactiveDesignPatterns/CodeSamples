/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03;

// 代码清单3-4 引用不透明
public class UsingStringBuffer {
  public static void main(String[] args) {
    // #snip
    final StringBuffer original = new StringBuffer("foo");
    final StringBuffer reversed = original.reverse();
    System.out.println(String.format("original '%s', new value '%s'", original, reversed));
    // #snip
  }
}
