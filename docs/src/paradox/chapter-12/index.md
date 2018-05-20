# 第12章——容错及恢复模式

在这一章中，你将学习到在设计应用程序时如何应对失败出现的可能性。我们将通过具体地构建一个具有回弹性的计算引擎来演示几个相应的模式，这个系统允许提交批处理作业，并在有着弹性调度的硬件资源上执行。我们将基于你在第6章和第7章所学习到的知识进行展开，因此，你可能想要回顾一下之前的内容。


## 12.4. 断路器模式

>代码清单 12-1 利用断路器使得失败组件有时间恢复

@@snip[代码清单12-1](../../../../chapter12/src/main/scala/chapter12/StorageComponent.scala){ #snip }

>代码清单12-2 使用速率限制器保护组件

@@snip[代码清单12-2](../../../../chapter12/src/main/scala/chapter12/RateLimiter.scala){ #snip }

>代码清单12-3 断路器：限制来自同一个客户端的请求

@@snip[代码清单12-3](../../../../chapter12/src/main/scala/chapter12/StorageClient.scala){ #snip }

>代码清单12-4 门控一个客户端

@@snip[代码清单12-4](../../../../chapter12/src/main/scala/chapter12/GatedStorageClient.scala){ #snip }