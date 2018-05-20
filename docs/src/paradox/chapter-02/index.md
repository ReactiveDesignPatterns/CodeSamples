# 第2章——反应式宣言概览

这一章详细地介绍了《反应式宣言》：原文文字简练且内容紧凑，我们将在这里加以展开并进行深入地讨论。有关该宣言相关理论的更多背景知识，请参阅本书第二部分。

## 2.1 对用户作出反应

>代码清单 2-1 图片服务中简单 Controller 的一段代码

@@snip[代码清单2-1](../../../../chapter02/src/main/java/ImageServiceController.java){ #snip }

## 2.2 利用并行性

>SimpleFunction.scala

@@snip[代码清单2-2](../../../../chapter02/src/main/scala/SimpleFunction.scala){ #snip }

>SequentialExecution.java

@@snip[代码清单2-3](../../../../chapter02/src/main/java/SequentialExecution.java){ #snip }

>ParallelExecutionWithJavaFuture.java

@@snip[代码清单2-4](../../../../chapter02/src/main/java/ParallelExecutionWithJavaFuture.java){ #snip }

>ParallelExecutionWithScalaFuture.scala

@@snip[代码清单2-5](../../../../chapter02/src/main/scala/ParallelExecutionWithScalaFuture.scala){ #snip }

>BlockingSocketRead.java

@@snip[代码清单2-6](../../../../chapter02/src/main/java/BlockingSocketRead.java){ #snip }

>AskActorWithJava8.java

@@snip[代码清单2-7](../../../../chapter02/src/main/java/AskActorWithJava8.java){ #snip }

## 2.4 对失败作出反应

>ExceptionHandler.scala

@@snip[代码清单2-8](../../../../chapter02/src/main/scala/ExceptionHandler.scala){ #snip }









