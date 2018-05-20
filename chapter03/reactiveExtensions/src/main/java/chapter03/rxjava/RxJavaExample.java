/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
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
