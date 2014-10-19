package org.reactivedesignpatterns.chapter2.rxjava;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RxJavaExampleTest {
    final RxJavaExample rxJavaExample = new RxJavaExample();

    @Test
    public void testRxJava() {
        String[] strings = { "a", "b", "c" };
        rxJavaExample.observe(strings);
    }
}
