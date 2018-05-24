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

@@snip[代码清单11-5](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-5 }

> 代码清单 11-6 用 Ask 模式并行地生成测试样本

@@snip[代码清单11-6](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-6 }

> 代码清单 11-7 使用自定义 Actor 来限制并行测试样本的数量

@@snip[代码清单11-7](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-7 }

> 代码清单 11-8 验证没有收到额外的消息

@@snip[代码清单11-8](../../../../chapter11/src/test/scala/chapter11/SchedulerSpec.scala){ #snip_11-8 }

> 代码清单 11-9 对具有关联 ID 的请求匹配响应

@@snip[代码清单11-9](../../../../chapter11/src/test/scala/chapter11/DataIngesterSpec.scala){ #snip }

> 代码清单 11-10 强制同步执行：仅对非阻塞处理安全

@@snip[代码清单11-10](../../../../chapter11/src/test/scala/chapter11/TranslationServiceSpec.scala){ #snip_11-10 }

> 代码清单 11-11 使用 CallingThreadDispatcher 来处理调用线程上的消息

@@snip[代码清单11-11](../../../../chapter11/src/test/scala/chapter11/ActorSpecWithCallingThreadDispatcher.scala){ #snip }

> 代码清单 11-12 将超时参数移到外部配置

@@snip[代码清单11-12](../../../../chapter11/src/test/scala/chapter11/TranslationServiceSpec.scala){ #snip_11-12 }

> 代码清单 11-13 异步地处理响应，从而创建完全反应式的测试

@@snip[代码清单11-13](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-13 }

> 代码清单 11-14 使用 async 和 await 提高异步测试的可读性

@@snip[代码清单11-14](../../../../chapter11/src/test/scala/chapter11/AsyncAwaitSpec.scala){ #snip }

> 代码清单 11-15 在 JavaScript 中测试翻译服务

@@snip[代码清单11-15](../../../../chapter11/src/test/scala/chapter11/js/translation.js){ #snip }

> 代码清单 11-16 使用请求-响应工厂来生成测试流量

@@snip[代码清单11-16](../../../../chapter11/src/test/scala/chapter11/EchoServiceSpec.scala){ #snip_11-16 }

> 代码清单 11-17 简单的翻译 API

@@snip[代码清单11-17](../../../../chapter11/src/main/scala/chapter11/TranslationService.scala){ #snip_11-17 }

> 代码清单 11-18 将更严格的类型添加到翻译 API

@@snip[代码清单11-18](../../../../chapter11/src/main/scala/chapter11/TranslationService.scala){ #snip_11-18 }

> 代码清单 11-19 测试翻译版本适配器

@@snip[代码清单11-19](../../../../chapter11/src/test/scala/chapter11/TranslationServiceSpec.scala){ #snip_11-19 }

> 代码清单 11-20 模拟错误过程

@@snip[代码清单11-20](../../../../chapter11/src/test/scala/chapter11/TranslationServiceSpec.scala){ #snip_11-20 }

> 代码清单 11-21 测试正确的错误处理

@@snip[代码清单11-21](../../../../chapter11/src/test/scala/chapter11/TranslationServiceSpec.scala){ #snip_11-21 }

## 11.6 测试回弹性

> 代码清单 11-22 要测试的基本 Actor

@@snip[代码清单11-22](../../../../chapter11/src/test/scala/chapter11/StepParentSpec.scala){ #snip_11-22 }

> 代码清单 11-23 为被测 Actor 提供测试上下文

@@snip[代码清单11-23](../../../../chapter11/src/test/scala/chapter11/StepParentSpec.scala){ #snip_11-23 }

> 代码清单 11-24 在 StepParent 的上下文中测试 Actor

@@snip[代码清单11-24](../../../../chapter11/src/test/scala/chapter11/StepParentSpec.scala){ #snip_11-24 }

> 代码清单 11-25 将失败报告给指定的 Actor

@@snip[代码清单11-25](../../../../chapter11/src/test/scala/chapter11/FailureParentSpec.scala){ #snip_11-25 }

> 代码清单 11-26 在测试中移除监督

@@snip[代码清单11-26](../../../../chapter11/src/test/scala/chapter11/FailureParentSpec.scala){ #snip_11-26 }







