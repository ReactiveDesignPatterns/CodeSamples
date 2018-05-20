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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 代码清单3-8
public class UsingMapFunction {
  public static void main(String[] args) {
        // #snip
        final List<Integer> numbers = Arrays.asList(1, 2, 3);
        final List<Integer> numbersPlusOne =
                numbers.stream()
                        .map(number -> number + 1)
                        .collect(Collectors.toList());
        // #snip
        System.out.println(numbersPlusOne.toString());
    }
}
