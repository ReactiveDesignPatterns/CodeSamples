# 第3章——行业主流工具

## 可用性 99.9999999%

3.1536 seconds of downtime in 100 years
 
@@snip[片段3-1](../../../../chapter03/snips/snip3-1.md){ #snip }

## 代码清单3-1

Listing 3.1 Unsafe, mutable message class, which may hide unexpected behavior

@@snip[代码清单3-1](../../../../chapter03/src/main/java/chapter03/Unsafe.java){ #snip }

## 代码清单3-2

Listing 3.2 Immutable message class that behaves predictably and is easier to reason about

@@snip[代码清单3-2](../../../../chapter03/src/main/java/chapter03/Immutable.java){ #snip }

## 代码清单3-3

使用Scala的case class定义的不可变消息

@@snip[代码清单3-3](../../../../chapter03/src/main/scala/chapter03/Message.scala){ #snip }

## 代码清单3-4

引用不透明

@@snip[代码清单3-4](../../../../chapter03/src/main/java/chapter03/UsingStringBuffer.java){ #snip }

## 代码清单3-5

引用透明

Referential transparency: allowing substitution of precomputed values

@@snip[代码清单3-5](../../../../chapter03/src/main/java/chapter03/Rooter.java){ #snip }

## 代码清单3-6

Limiting usability with side effects

@@snip[代码清单3-6](../../../../chapter03/src/main/java/chapter03/SideEffecting.java){ #snip }