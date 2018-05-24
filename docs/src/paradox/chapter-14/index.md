# 第14章——资源管理模式

大多数系统都面临着一个共同的问题，那就是你需要管理或展现资源：文件存储空间、计算能力、对数据库或 Web API 的访问、如打印机和读卡器等物理设备，诸如此类。你所创建的某个组件，可能会独自为系统的其余部分提供某种资源，又或者你可能需要将其与外部资源整合。在这一章中，我们将讨论处理反应式应用程序中资源的模式。我们将着重讨论以下几种模式：

## 14.1 资源封装模式

> 代码清单 14-1 亚马逊EC2实例作为工作者节点

@@snip[代码清单14-1](../../../../chapter14/src/main/java/chapter14/ResourceEncapsulation.java){ #snip_14-1 }

> 代码清单 14-2 将EC2节点提升到一个Future中，从而简化失败处理过程

@@snip[代码清单14-2](../../../../chapter14/src/main/java/chapter14/ResourceEncapsulation.java){ #snip_14-2 }

> 代码清单 14-3 通过桥接客户端代码执行亚马逊的异步客户端

@@snip[代码清单14-3](../../../../chapter14/src/main/java/chapter14/ResourceEncapsulation.java){ #snip_14-3 }

> 代码清单 14-4 关闭 EC2 实例

@@snip[代码清单14-4](../../../../chapter14/src/main/java/chapter14/ResourceEncapsulation.java){ #snip_14-4 }

> 代码清单 14-5 执行组件和看作工作者节点的 Actor 通信

@@snip[代码清单14-5](../../../../chapter14/src/main/java/chapter14/ResourceEncapsulation.java){ #snip_14-5 }

## 14.2 资源借贷模式

> 代码清单 14-6 分离资源和任务的管理

@@snip[代码清单14-6](../../../../chapter14/src/main/java/chapter14/ResourceLoan.java){ #snip_14-6 }

## 14.3 复杂命令模式

> 代码清单 14-7 批处理作业的基本构成

@@snip[代码清单14-7](../../../../chapter14/src/main/java/chapter14/ComplexCommand.java){ #snip_14-7 }

> 代码清单 14-8 通过调用Nashorn JavaScript脚本引擎执行处理逻辑

@@snip[代码清单14-8](../../../../chapter14/src/main/java/chapter14/ComplexCommand.java){ #snip_14-8 }

> job.js

@@snip[job.js](../../../../chapter14/src/main/resources/chapter14/job.js){ #snip }

> 代码清单 14-9 外部 DSL 使用了不同于宿主编程语言的语法

@@snip[代码清单14-9](../../../../chapter14/src/main/resources/chapter14/dsl.txt){ #snip }

> 代码清单 14-10 内部 DSL

@@snip[代码清单14-10](../../../../chapter14/src/main/java/chapter14/ComplexCommand.java){ #snip_14-10 }

## 14.5 托管阻塞模式

> 代码清单 14-11 维护一个私有的ExecutorService

@@snip[代码清单14-11](../../../../chapter14/src/main/java/chapter14/ManagedBlocking.java){ #snip_14-11 }

