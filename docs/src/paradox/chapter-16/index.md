# 第16章——流控制模式

在前面的章节里，你学会了如何将系统拆分成较小的部分，以及这些部分之间如何通信以解决较大的任务。有一个我们到目前为止尚未提及的角度：除了确定谁与谁交互之外，你必须同样考虑通信的时效性。为了让你的系统在不同的负载下都更具有回弹性，你需要能够阻止组件因为过量的请求速率而不可控制地失败的机制。为此，这一章介绍了下面四种基本模式。

## 16.1  拉取模式

>代码清单16.1 在拉取输入的工作者内部处理昂贵的计算

@@snip[代码清单16-1](../../../../chapter16/src/main/scala/chapter16/PullPattern.scala){ #snip_16-1 }

>代码清单16-2 按工作者所请求的数目给它们提供任务

@@snip[代码清单16-2](../../../../chapter16/src/main/scala/chapter16/PullPattern.scala){ #snip_16-2 }

## 16.2 托管队列模式

>代码清单16-3 管理一个工作队列以对过载作出反应

@@snip[代码清单16-3](../../../../chapter16/src/main/scala/chapter16/QueuePattern.scala){ #snip_16-3 }

>QueuePattern.scala

@@snip[代码清单16-3](../../../../chapter16/src/main/scala/chapter16/QueuePattern.scala){ #snip_16-3 }

## 16.3 丢弃模式

>DropPattern.scala

@@snip[代码清单16-3](../../../../chapter16/src/main/scala/chapter16/DropPattern.scala){ #snip_1 }

>DropPattern.scala

@@snip[代码清单16-3](../../../../chapter16/src/main/scala/chapter16/DropPattern.scala){ #snip_2 }

>DropPatternWithProtection.scala

@@snip[代码清单16-3](../../../../chapter16/src/main/scala/chapter16/DropPatternWithProtection.scala){ #snip_1 }

## 16.4 限流模式

>代码清单16-4 根据特定速率使用令牌桶来拉取工作

@@snip[代码清单16-4](../../../../chapter16/src/main/scala/chapter16/ThrottlingPattern.scala){ #snip_16-4 }


