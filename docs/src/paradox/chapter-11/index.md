# 第11章——测试反应式应用程序

## 11.3 异步测试

> 代码清单 11-1 测试一个完全同步的翻译函数

@@snip[代码清单11-1](../../../../chapter11/src/test/scala/chapter11/SynchronousSpec.scala){ #snip }

> AsyncSpec.scala

@@snip[AsyncSpec.scala](../../../../chapter11/src/test/scala/chapter11/AsyncSpec.scala){ #snip }

> ActorSpec.scala

@@snip[ActorSpec.scala](../../../../chapter11/src/test/scala/chapter11/ActorSpec.scala){ #snip }

> AsyncSpecWithWhile.scala

@@snip[AsyncSpecWithWhile.scala](../../../../chapter11/src/test/scala/chapter11/AsyncSpecWithWhile.scala){ #snip }

> AsyncSpecWithWhileLoopIterationsBounded.scala

@@snip[AsyncSpecWithWhileLoopIterationsBounded.scala](../../../../chapter11/src/test/scala/chapter11/AsyncSpecWithWhileLoopIterationsBounded.scala){ #snip }

> 代码清单 11-2 等待结果的动作在翻译过程中同步阻塞

@@snip[代码清单11-2](../../../../chapter11/src/test/scala/chapter11/AsyncSpecWithAwait.scala){ #snip }

> 代码清单 11-3 使用 TestProbe 预期答复

@@snip[代码清单11-3](../../../../chapter11/src/test/scala/chapter11/ActorSpecWithExpectMsg.scala){ #snip }

> 代码清单 11-4 使用 TestProbe 接收调度程序的响应

@@snip[代码清单11-4](../../../../chapter11/src/test/scala/chapter11/SchedulerSpec.scala){ #snip }

> 代码清单 11-5 确定第 95 百分位的延迟

@@snip[代码清单11-5](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-9 }

> 代码清单 11-6 用 Ask 模式并行地生成测试样本

@@snip[代码清单11-6](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-10 }

> 代码清单 11-7 使用自定义 Actor 来限制并行测试样本的数量

@@snip[代码清单11-7](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-11 }

> 代码清单 11-8 验证没有收到额外的消息

@@snip[代码清单11-12](../../../../chapter11/src/test/scala/chapter11/SchedulerSpec.scala){ #snip_11-12 }

## 代码清单11-13

Listing 11.9 Matching responses to requests with a correlation ID

@@snip[代码清单11-13](../../../../chapter11/src/test/scala/chapter11/DataIngesterSpec.scala){ #snip }





