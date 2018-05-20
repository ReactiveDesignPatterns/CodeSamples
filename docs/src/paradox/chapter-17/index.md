# 第17章——状态管理和持久化模式

前一章介绍了消息速率、负载和时间的各种概念；我们之前只考虑了不同组件之间和时间无关的关联。这一章增加了另一个正交维度来完成这整个版图：维持状态几乎是所有组件的共同目的，而我们还没有讨论应该怎么做到这一点。这里所呈现的模式彼此密切相关，并形成了一个有机整体。

## 17.1 领域对象模式

>DomainObject.scala

@@snip[代码清单17-1](../../../../chapter17/src/main/scala/chapter17/DomainObject.scala){ #snip }

>清单 17-1 一个最小化的购物车定义

@@snip[代码清单17-1](../../../../chapter17/src/main/scala/chapter17/DomainObject.scala){ #snip_17-1 }

>清单 17-2 用于和购物车对象通信的消息

@@snip[代码清单17-2](../../../../chapter17/src/main/scala/chapter17/ShoppingCartMessage.scala){ #snip_17-2 }

>清单 17-3 一个购物车管理者 Actor

@@snip[代码清单17-3](../../../../chapter17/src/main/scala/chapter17/ObjectManager.scala){ #snip_17-3 }

## 17.2 分片模式

>清单 17-4 为购物车定义分片算法

@@snip[代码清单17-4](../../../../chapter17/src/main/scala/chapter17/Sharding.scala){ #snip_17-4 }

>清单 17-5 启动一个群集来托管分片

@@snip[代码清单17-5](../../../../chapter17/src/main/scala/chapter17/Sharding.scala){ #snip_17-5 }

>Sharding.scala

@@snip[代码清单17-5](../../../../chapter17/src/main/scala/chapter17/Sharding.scala){ #snip }

## 17.3 事件溯源模式

>清单 17-6 将领域事件添加到业务逻辑

@@snip[代码清单17-6](../../../../chapter17/src/main/scala/chapter17/DomainObject.scala){ #snip_17-1 }

>清单 17-7 持久化一个事件溯源领域对象

@@snip[代码清单17-7](../../../../chapter17/src/main/scala/chapter17/EventSourcing.scala){ #snip_17-7 }

## 17.4 事件流模式

>清单 17-8 在写日志期间对事件打标签

@@snip[代码清单17-8](../../../../chapter17/src/main/scala/chapter17/EventStream.scala){ #snip_17-8 }

>清单 17-9 一个正在监听事件流的 Actor

@@snip[代码清单17-9](../../../../chapter17/src/main/scala/chapter17/EventStream.scala){ #snip_17-9 }
