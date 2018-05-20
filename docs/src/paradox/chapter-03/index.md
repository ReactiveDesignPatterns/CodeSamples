# 第3章——行业工具

## 3.2 函数式编程

> 代码清单3-1 不安全的、可变的消息类，可能会隐含非预期的行为

@@snip[代码清单3-1](../../../../chapter03/src/main/java/chapter03/Unsafe.java){ #snip }

>代码清单 3-2 不可变的消息类，其行为是可预知的，并易于推断

@@snip[代码清单3-2](../../../../chapter03/src/main/java/chapter03/Immutable.java){ #snip }

>Message.scala

@@snip[代码清单3-3](../../../../chapter03/src/main/scala/chapter03/Message.scala){ #snip }

>UsingStringBuffer.java

@@snip[代码清单3-4](../../../../chapter03/src/main/java/chapter03/UsingStringBuffer.java){ #snip }

>代码清单 3-3 引用透明性：允许代换预先计算好的值

@@snip[代码清单3-5](../../../../chapter03/src/main/java/chapter03/Rooter.java){ #snip }

>代码清单3-4 因副作用而受限的可用性

@@snip[代码清单3-6](../../../../chapter03/src/main/java/chapter03/SideEffecting.java){ #snip }

>IntSeeding.java

@@snip[代码清单3-7](../../../../chapter03/src/main/java/chapter03/IntSeeding.java){ #snip }

>UsingMapFunction.java

@@snip[代码清单3-8](../../../../chapter03/src/main/java/chapter03/UsingMapFunction.java){ #snip }

*Python REPL*

@@snip[Python REPL 3-1](../../../../chapter03/src/main/python/output/myFunction.py.output){ #output }

## 3.4 对反应式设计的现有支持

>sample.js

@@snip[代码清单3-9](../../../../chapter03/eventloop/src/main/js/sample.js){ #snip }

>sample.go

@@snip[代码清单3-10](../../../../chapter03/csp/src/main/go/sample.go){ #snip }

>代码清单3-5 从更快的数据源获取结果

@@snip[代码清单3-11](../../../../chapter03/futures-and-promises/src/main/java/chapter03/future/ParallelRetrievalExample.java){ #snip }

>代码清单3-6 使用Scala编程语言将两个Future的结果组合为单一结果

@@snip[代码清单3-12](../../../../chapter03/futures-and-promises/src/main/scala/chapter03/future/StagedFuturesForExample.scala){ #snip }

>StagedFuturesAsyncExample.scala

@@snip[代码清单3-13](../../../../chapter03/futures-and-promises/src/main/scala/chapter03/future/StagedFuturesAsyncExample.scala){ #snip }

>RxJavaExample.java

@@snip[代码清单3-14](../../../../chapter03/reactiveExtensions/src/main/java/chapter03/rxjava/RxJavaExample.java){ #snip }

>RxJavaExampleDriver.java

@@snip[代码清单3-15](../../../../chapter03/reactiveExtensions/src/main/java/chapter03/rxjava/RxJavaExampleDriver.java){ #snip }

>代码清单3-7 一个使用 Akka 的Actor的例子

@@snip[代码清单3-16](../../../../chapter03/actor/src/main/scala/chapter03/actor/Example.scala){ #snip }







