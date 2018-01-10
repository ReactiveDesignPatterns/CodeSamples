# 第11章——测试反应式应用程序

## 代码清单11-1

Listing 11.1 Testing a purely synchronous translation function

@@snip[代码清单11-1](../../../../chapter11/src/test/scala/chapter11/SynchronousSpec.scala){ #snip }

## 代码清单11-2

@@snip[代码清单11-2](../../../../chapter11/src/test/scala/chapter11/AsyncSpec.scala){ #snip }

## 代码清单11-3

@@snip[代码清单11-3](../../../../chapter11/src/test/scala/chapter11/ActorSpec.scala){ #snip }

## 代码清单11-4

@@snip[代码清单11-4](../../../../chapter11/src/test/scala/chapter11/AsyncSpecWithWhile.scala){ #snip }

## 代码清单11-5

@@snip[代码清单11-5](../../../../chapter11/src/test/scala/chapter11/AsyncSpecWithWhileLoopIterationsBounded.scala){ #snip }

## 代码清单11-6

Listing 11.2 Awaiting the result blocks synchronously on the translation

@@snip[代码清单11-6](../../../../chapter11/src/test/scala/chapter11/AsyncSpecWithAwait.scala){ #snip }

## 代码清单11-7

Listing 11.3 Expecting replies with a TestProbe

@@snip[代码清单11-7](../../../../chapter11/src/test/scala/chapter11/ActorSpecWithExpectMsg.scala){ #snip }

## 代码清单11-8

Listing 11.4 Using a TestProbe to receive the response from the scheduler

@@snip[代码清单11-8](../../../../chapter11/src/test/scala/chapter11/SchedulerSpec.scala){ #snip }



