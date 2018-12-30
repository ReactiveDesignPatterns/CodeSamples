# 勘误

>本书的勘误将第一时间在这个页面进行同步。并按照章节和页码的升序进行排列。

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

