/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

