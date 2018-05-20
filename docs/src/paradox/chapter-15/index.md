# 第15章——消息流模式

在这一章中，我们将探讨一些存在于反应式组件之间最基本的通信模式：具体而言，我们将讨论消息是如何在它们之间流动的。在第10章中，我们讨论了理论背景，提到了系统中的通信路径设计对于系统的成功来说至关重要——无论是对现实世界的组织，还是反应式应用程序。

## 15.1 请求——响应模式

>代码清单15-1 服务器向发起请求的地址发送响应

@@snip[代码清单15-1](../../../../chapter15/src/main/java/chapter15/Server.java){ #snip }

>代码清单15-2 客户端发送一个请求，然后阻塞直到接收到服务器的响应

@@snip[代码清单15-2](../../../../chapter15/src/main/java/chapter15/Client.java){ #snip }

*Server 的输出结果*

@@snip[代码清单15-1](../../../../chapter15/src/main/output/server.output){ #snip }

*Client 的可能输出结果*

@@snip[代码清单15-2](../../../../chapter15/src/main/output/client.output){ #snip }

*HTTP Request Header*

@@snip[HTTP Request Header](../../../../chapter15/src/main/output/http-request.header){ #snip }

*HTTP Response Header*

@@snip[HTTP Response Header](../../../../chapter15/src/main/output/http-response.header){ #snip }

>代码清单15-3 使用Akka UntypedActor 来建模请求——响应模式

@@snip[代码清单15-3](../../../../chapter15/src/main/scala/chapter15/RequestResponseActors.scala){ #snip }

*运行结果*

@@snip[代码清单15-3](../../../../chapter15/src/main/output/request-response-actors.output){ #snip }

>代码清单15-4 在请求消息中显式地包含响应地址

@@snip[代码清单15-4](../../../../chapter15/src/main/scala/chapter15/RequestResponseTypedActors.scala){ #snip }

>代码清单15-5 基于单向消息传递的请求——响应模式

@@snip[代码清单15-5](../../../../chapter15/src/main/js/request-response.js){ #snip }

>代码清单15-6 监听与原始请求具有相同的关联ID的响应

@@snip[代码清单15-6](../../../../chapter15/src/main/js/request-response-2way.js){ #snip }


## 15.2 消息自包含模式

*SMTP*

@@snip[SMTP](../../../../chapter15/src/main/output/smtp.protocal){ #snip }

>代码清单15-7 封装了多次 SMTP 交换所需要的信息

@@snip[代码清单15-7](../../../../chapter15/src/main/scala/chapter15/SendEmail.scala){ #snip }

@@snip[代码清单15-7](../../../../chapter15/src/main/scala/chapter15/Result.scala){ #snip }

>代码清单15-8 分离电子邮件主体，使得其可以按需投递

@@snip[代码清单15-8](../../../../chapter15/src/main/scala/chapter15/streamed/SendEmail.scala){ #snip }

>代码清单15-9 允许邮件主体可以被接收者拉取

@@snip[代码清单15-9](../../../../chapter15/src/main/scala/chapter15/pullable/SendEmail.scala){ #snip }

## 15.3 询问模式

>代码清单15-10 请求启动验证过程的简单协议

@@snip[代码清单15-10](../../../../chapter15/src/main/scala/chapter15/pattern/ask/Protocal.scala){ #snip }

>代码清单15-11 一个转发结果的匿名子Actor

@@snip[代码清单15-11](../../../../chapter15/src/main/scala/chapter15/pattern/ask/AskPattern.scala){ #snip_15-11 }

>代码清单15-12 由询问模式所产生的Future，并进行了转换

@@snip[代码清单15-12](../../../../chapter15/src/main/scala/chapter15/pattern/ask/AskPattern.scala){ #snip_15-12 }

>代码清单15-13 不使用内置支持实现询问模式

@@snip[代码清单15-13](../../../../chapter15/src/main/scala/chapter15/pattern/ask/AskPattern.scala){ #snip_15-13 }

## 15.5 聚合器模式

>代码清单15-14 使用for推导式来聚合三个Future表达式的结果。

@@snip[代码清单15-14](../../../../chapter15/src/main/scala/chapter15/Aggregator.scala){ #snip_15-14 }

>代码清单15-15 使用子Actor替代Future组合子的使用

@@snip[代码清单15-15](../../../../chapter15/src/main/scala/chapter15/Aggregator.scala){ #snip_15-15 }

>代码清单15-16 使用一个构建器来更加直接地表达领域模型

@@snip[代码清单15-16](../../../../chapter15/src/main/scala/chapter15/Aggregator.scala){ #snip_15-16 }

>代码清单15-17 添加第4个服务，降低了代码的可读性

@@snip[代码清单15-17](../../../../chapter15/src/main/scala/chapter15/Aggregator.scala){ #snip_15-17 }

## 15.7 业务握手协议（又名可靠投递模式）

>代码清单15-18 使用Actor实现上面的（信息）交换

@@snip[代码清单15-18](../../../../chapter15/src/main/scala/chapter15/BusinessHandshake.scala){ #snip_15-18 }

>代码清单15-19 向预算消息添加持久性

@@snip[代码清单15-19](../../../../chapter15/src/main/scala/chapter15/BusinessHandshake.scala){ #snip_15-19 }

>代码清单15-20 Alice Actor的持久化版本

@@snip[代码清单15-20](../../../../chapter15/src/main/scala/chapter15/BusinessHandshake.scala){ #snip_15-20 }