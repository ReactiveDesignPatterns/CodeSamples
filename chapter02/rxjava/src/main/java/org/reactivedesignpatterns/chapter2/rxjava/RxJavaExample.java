package org.reactivedesignpatterns.chapter2.rxjava;

import io.reactivex.Observable;

public class RxJavaExample {
    RxJavaExample() {
    }

    public void observe(String[] strings) {
        Observable.fromArray(strings).subscribe((s) -> {
                System.out.println("Received " + s);
        });
    }
}
