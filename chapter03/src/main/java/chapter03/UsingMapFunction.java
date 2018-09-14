/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 代码清单3-8
public class UsingMapFunction {
  public static void main(String[] args) {
    // #snip
    final List<Integer> numbers = Arrays.asList(1, 2, 3);
    final List<Integer> numbersPlusOne =
        numbers.stream().map(number -> number + 1).collect(Collectors.toList());
    // #snip
    System.out.println(numbersPlusOne.toString());
  }
}
