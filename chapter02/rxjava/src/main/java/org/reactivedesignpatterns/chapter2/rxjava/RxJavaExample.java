package org.reactivedesignpatterns.chapter2.rxjava;

import rx.Observable;
import rx.functions.Action1;

public class RxJavaExample {
    RxJavaExample() {
    }

    public void observe(String[] strings) {
        Observable.from(strings).subscribe((s) -> {
                System.out.println("Received " + s);
        });
    }
}
