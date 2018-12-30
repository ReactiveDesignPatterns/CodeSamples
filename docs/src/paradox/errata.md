# 勘误

>本书的勘误将第一时间在这个页面进行同步。并按照章节和页码的升序进行排列。本书原著的勘误也会进行第一时间的合并，
在本书中文版出版时，我们已经合并了原著到2018年三月份的勘误。[外版勘误地址](https://manning-content.s3.amazonaws.com/download/5/7e201ec-e305-4493-817c-954eb5a3803c/Kuhn_ReactiveDesignPatterns_err2.html)

勘误的模板为
```
### 章节

#### 版次

#### 页码

#### 原文

#### 修正

#### 说明

--- 分隔符

```

----

### 全书代码清单更新

#### 版次

第一次印刷

#### 页码

所有涉及到`case class`的代码清单

#### 原文

```scala
case class
```

#### 修正
```scala
final case class
```

#### 说明

`case class` 如果不带 `final` 标识符，在 Java 中依然是可以继承的，造成非常不好的体验。通常来说，对于需要和 Java 进行交互的库来说，在编写时 `case class` 需要定义为 `final case class` 从而限定他们在 `Java` 中的可继承性。而对于 `case object`来说，因为天然等于加了`final`，所以不需要了。

----

### 第二章

#### 第一次印刷

#### 页码

15

#### 原文

```java
  public Images cache;
  public Images database;
```

#### 修正

```java
  private Images cache;
  private Images database;
```
#### 说明

这里暴露了`ImageServiceController`的内部成员，需要将可见性设置为`private`.

---

#### 第一次印刷

#### 页码

19

#### 原文

```java
    ReplyA a = computeA();
    ReplyB b = computeB();
    ReplyC c = computeC();

    Result r = aggregate(a, b, c);
```

#### 修正

```java
    final ReplyA a = computeA();
    final ReplyB b = computeB();
    final ReplyC c = computeC();

    final Result r = aggregate(a, b, c);
```
#### 说明

在反应式编程中，我们应该尽可能地利用不可变，类似于在 Scala 中，我们需要尽可能地使用 `val` 而不是 `var`,并且保持和 2.2.2 小节的 Scala 
代码清单对齐（其中使用的是`val`）。

---

#### 第一次印刷

#### 页码

20

#### 原文

```java
    Future<ReplyA> a = taskA();
    Future<ReplyB> b = taskB();
    Future<ReplyC> c = taskC();

    Result r = aggregate(a.get(), b.get(), c.get());
```

#### 修正

```java
    final Future<ReplyA> a = taskA();
    final Future<ReplyB> b = taskB();
    final Future<ReplyC> c = taskC();

    final Result r = aggregate(a.get(), b.get(), c.get());
```
#### 说明

在反应式编程中，我们应该尽可能地利用不可变，类似于在 Scala 中，我们需要尽可能地使用 `val` 而不是 `var`,并且保持和 2.2.2 小节的 Scala 
代码清单对齐（其中使用的是`val`）。

---

#### 第一次印刷

#### 页码

23

#### 原文

```java
    CompletionStage<Response> future =
        ask(actorRef, request, timeout).thenApply(Response.class::cast);
    future.thenAccept(AskActorWithJava8::processIt);
```

#### 修正

```java
    final CompletionStage<Response> future =
        ask(actorRef, request, timeout).thenApply(Response.class::cast);
    future.thenAccept(AskActorWithJava8::processIt);
```
#### 说明

在反应式编程中，我们应该尽可能地利用不可变，类似于在 Scala 中，我们需要尽可能地使用 `val` 而不是 `var`。

---

### 第三章

#### 第一次印刷

#### 页码

53

#### 原文

译者注 26 ：Java 8 并未内置... ,参见 vavr.io ...

#### 修正

Java 8 并未内置类名类似于“Promise”的实现，不过在 Netty 等流行的异步网络编程库中都有名为“Promise”的定义和实现。—— 译者注

#### 说明

在本书出版的时候，译者使用的是 vavr 的`0.9.2`版本,在这个版本中的确有 `Promise` 的定义和对应的实现，见：
`io.vavr.concurrent.Promise`，不过在目前的主干和接下来的1.0.0版本中，vavr的作者已经删除对应的实现，
所以这里的描述已经过时。

---

#### 第一次印刷

#### 页码

58

#### 原文

译者注 37：在 RXJava 2.x 的版本中....

#### 修正

在 RxJava 2.x 的版本中....

#### 说明

这里的`RxJava` 的第二个 `x` 需要小写，属于 typo。

---

#### 第一次印刷

#### 页码

59

#### 原文
```java
  private static final RxJavaExample rxJavaExample = new RxJavaExample();
```
和
```java
  rxJavaExample.observe(strings);
```

#### 修正

```java
  private static final RxJavaExample RX_JAVA_EXAMPLE = new RxJavaExample();
```
和
```java
  RX_JAVA_EXAMPLE.observe(strings);
```
#### 说明

静态的成员变量，应该推荐使用大写加下划线的形式。

---
### 第十一章

#### 第一次印刷

#### 页码

143

#### 原文

```scala
  val echo = echoService("keepSLAfuture")
```
#### 修正

```scala
  val echo = echoService("keepSLAWithFuture")
```
#### 说明

typo

---

#### 第一次印刷

#### 页码

144

#### 原文

```scala
  val echo = echoService("keepSLAparallel")
```

和
```scala
      val controller = system.actorOf(
        Props[ParallelSLATester],
        "keepSLAparallelController")
```
#### 修正

```scala
  val echo = echoService("keepSLAInParallel")
```
和
```scala
      val controller = system.actorOf(
        Props[ParallelSLATester],
        "keepSLAInParallelController")
```
#### 说明

typo

---

#### 第一次印刷

#### 页码

149

#### 原文

```scala
      val controller = system.actorOf(
        Props[ParallelSLATester],
        "keepSLAparallelController")
```
#### 修正

```scala
      val controller = system.actorOf(
        Props[ParallelSLATester],
        "keepSLAInParallelAndAsyncController")
```
#### 说明

1. typo
2. 因为在 `ParallelSLATester` 中，对应的`context.stop(self)`是一个异步动作，所以在快速运行多个测试的时候，
会出现偶发的：`akka.actor.InvalidActorNameException`，所以进行改名对当前的用例来说是比较直接安全的一种做法。
感兴趣的读者可以看下下面的错误信息：

```
[info] - must keep its SLA when used in parallel and handling responses asynchronously *** FAILED ***
[info]   akka.actor.InvalidActorNameException: actor name [keepSLAparallelController] is not unique!
[info]   at akka.actor.dungeon.ChildrenContainer$NormalChildrenContainer.reserve(ChildrenContainer.scala:129)
[info]   at akka.actor.dungeon.Children.reserveChild(Children.scala:134)
[info]   at akka.actor.dungeon.Children.reserveChild$(Children.scala:132)
[info]   at akka.actor.ActorCell.reserveChild(ActorCell.scala:431)
[info]   at akka.actor.dungeon.Children.makeChild(Children.scala:272)
[info]   at akka.actor.dungeon.Children.attachChild(Children.scala:48)
[info]   at akka.actor.dungeon.Children.attachChild$(Children.scala:47)
[info]   at akka.actor.ActorCell.attachChild(ActorCell.scala:431)
[info]   at akka.actor.ActorSystemImpl.actorOf(ActorSystem.scala:753)
[info]   at chapter11.EchoServiceSpec.$anonfun$new$10(EchoServiceSpec.scala:242)
```

[原始链接](https://github.com/ReactivePlatform/Reactive-Design-Patterns/pull/45#issuecomment-450505641)

---

### 第十三章

#### 第一次印刷

#### 页码

220

#### 原文

代码清单 13-15：

```scala
  case SeqResult(_, res, replica, _) if res != right ⇒ replica
```
#### 修正

```scala
  case SeqResult(_, result, replica, _) if res != right ⇒ replica
```
#### 说明

代码清单中的这个部分，因为`res` shallow 了外面的 `add`方法中的`res`参数，所以
实际上比较的类型是不相关的，会造成状态无法收敛到`Known`。通过重新命名解决了这个问题。

---

### 第十四章

#### 第一次印刷

#### 页码

231

#### 原文

代码清单 14-1：

```java
    AmazonEC2 amazonEC2Client = new AmazonEC2Client(credentials);
```
#### 修正

```java
    final AmazonEC2 amazonEC2Client =
        AmazonEC2ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
```
#### 说明

切换到新版本客户端的推荐写法，旧版本写法已经废弃。其他包括代码清单中添加了`final`关键字。

---

#### 第一次印刷

#### 页码

235

#### 原文

代码清单 14-5：

```java
    public WorkerNode(final InetAddress address, final FiniteDuration checkInterval) {
      checkTimer =
          getContext()
              .system()
              .scheduler()
              .schedule(
                  checkInterval,
                  checkInterval,
                  self(),
                  DoHealthCheck.instance,
                  getContext().dispatcher(),
                  self());
    }
```
    
#### 修正

```java
    public WorkerNode(final InetAddress address, final Duration checkInterval) {
      checkTimer =
          getContext()
              .getSystem()
              .getScheduler()
              .schedule(
                  checkInterval,
                  checkInterval,
                  self(),
                  DoHealthCheck.INSTANCE,
                  getContext().dispatcher(),
                  self());
    }
```
#### 说明

切换到新版本 Akka 中更加 Java native 的写法。并且针对静态类成员 `DoHealthCheck.instance` 应用大写格式。

---

#### 第一次印刷

#### 页码

235

#### 原文

代码清单 14-5：

```java
    private PartialFunction<Object, BoxedUnit> initialized()
```
    
#### 修正

```java
    private Receive initialized()
```
#### 说明

切换到新版本 Akka 中更加 Java native 的写法。更加直接地是用 `AbstractActor` 提供的基础设施。

---

#### 第一次印刷

#### 页码

247

#### 原文

代码清单 14-8：

```java
    private static final ScriptEngine engine = 
```
    
#### 修正

```java
    private static final ScriptEngine ENGINE = 
```
#### 说明

对于类的静态成员，切换为使用全大写的格式。其他包含一些`final`关键字的增加。

---