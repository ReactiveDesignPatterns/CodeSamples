# 第13章——复制模式

## 13.1 主动-被动复制模式

> Protocol

@@snip[Protocol](../../../../chapter13/src/main/scala/chapter13/KVStoreUtils.scala){ #snip_protocol }

> 代码清单 13-1 单例作为主动副本来接管

@@snip[代码清单13-1](../../../../chapter13/src/main/scala/chapter13/ActivePassive.scala){ #snip_13-1 }

> 代码清单 13-2 主动副本传播复制请求

@@snip[代码清单13-2](../../../../chapter13/src/main/scala/chapter13/ActivePassive.scala){ #snip_13-2 }

> 代码清单 13-3 通过将JSON文件写入到本地磁盘来实现持久化

@@snip[代码清单13-3](../../../../chapter13/src/main/scala/chapter13/KVStoreUtils.scala){ #snip_13-3 }

> 代码清单 13-4 被动副本追踪它们是否是最新的版本

@@snip[代码清单13-4](../../../../chapter13/src/main/scala/chapter13/ActivePassive.scala){ #snip_13-4 }

> 代码清单 13-5 被动副本在滞后过多时请求一份全量更新

@@snip[代码清单13-5](../../../../chapter13/src/main/scala/chapter13/ActivePassive.scala){ #snip_13-5 }

> 代码清单 13-6 计算直接可应用的队列前段的长度

@@snip[代码清单13-6](../../../../chapter13/src/main/scala/chapter13/ActivePassive.scala){ #snip_13-6 }

> 代码清单 13-7 确定更新队列里的数据缺口是否可以被一一填补

@@snip[代码清单13-7](../../../../chapter13/src/main/scala/chapter13/ActivePassive.scala){ #snip_13-7 }

## 13.2 多主复制模式

> 代码清单 13-8 使用 CKite 来实现键值存储

@@snip[代码清单13-8](../../../../chapter13/src/main/scala/ckite/KVStore.scala){ #snip_13-8 }

> 代码清单 13-9 按照复制状态机来实例化KVStore

@@snip[代码清单13-9](../../../../chapter13/src/main/scala/ckite/KVStore.scala){ #snip_13-9 }

> ckite api

@@snip[ckite_api](../../../../chapter13/src/main/scala/ckite/KVStore.scala){ #snip_ckite_api }

> 代码清单 13-10 图13-1中图形的代码表示

@@snip[代码清单13-10](../../../../chapter13/src/main/scala/chapter13/MultiMasterCRDT.scala){ #snip_13-10 }

> 代码清单 13-11 合并两个状态来产生第三个合并后的状态

@@snip[代码清单13-11](../../../../chapter13/src/main/scala/chapter13/MultiMasterCRDT.scala){ #snip_13-11 }

> 代码清单 13-12 使用 Akka Distributed Data 来传播状态变更

@@snip[代码清单13-12](../../../../chapter13/src/main/scala/chapter13/MultiMasterCRDT.scala){ #snip_13-12 }

> 代码清单 13-13 引入对于任务的请求标志

@@snip[代码清单13-13](../../../../chapter13/src/main/scala/chapter13/MultiMasterCRDT.scala){ #snip_13-13 }

## 13.3 主动-主动复制模式

> 代码清单 13-14 用无协调工作的实现来开始主动-主动复制模式

@@snip[代码清单13-14](../../../../chapter13/src/main/scala/chapter13/ActiveActive.scala){ #snip_13-14 }

> 代码清单 13-15 封装对于单个客户端请求的知悉情况

@@snip[代码清单13-15](../../../../chapter13/src/main/scala/chapter13/ActiveActive.scala){ #snip_13-15 }

> 代码清单 13-16 将副本当作子Actor管理

@@snip[代码清单13-16](../../../../chapter13/src/main/scala/chapter13/ActiveActive.scala){ #snip_13-16 }

> 代码清单 13-17 按序发送回复

@@snip[代码清单13-17](../../../../chapter13/src/main/scala/chapter13/ActiveActive.scala){ #snip_13-17 }

> 代码清单 13-18 一旦timeout，就强迫将“missing”回复转为“known”回复

@@snip[代码清单13-18](../../../../chapter13/src/main/scala/chapter13/ActiveActive.scala){ #snip_13-18 }

> 代码清单 13-19 终止并替换未完成的副本

@@snip[代码清单13-19](../../../../chapter13/src/main/scala/chapter13/ActiveActive.scala){ #snip_13-19 }
