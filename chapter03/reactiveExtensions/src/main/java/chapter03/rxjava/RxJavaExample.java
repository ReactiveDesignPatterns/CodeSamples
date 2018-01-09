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

// 代码清单3-14
// #snip
package chapter03.rxjava;

import io.reactivex.Observable;

public class RxJavaExample {
    public void observe(String[] strings) {
        Observable.fromArray(strings).subscribe((s) -> {
                System.out.println("Received " + s);
        });
    }
}
// #snip
