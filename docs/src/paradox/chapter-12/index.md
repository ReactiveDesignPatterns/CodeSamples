# 第12章——容错及恢复模式

## 代码清单12-1

Listing 12.1 Using a circuit breaker to give a failed component time to recover

@@snip[代码清单12-1](../../../../chapter12/src/main/scala/chapter12/StorageComponent.scala){ #snip }

## 代码清单12-2

Listing 12.2 Protecting a component by using a rate limiter

@@snip[代码清单12-2](../../../../chapter12/src/main/scala/chapter12/RateLimiter.scala){ #snip }

## 代码清单12-3

Listing 12.3 Protecting a component by using a rate limiter

@@snip[代码清单12-3](../../../../chapter12/src/main/scala/chapter12/StorageClient.scala){ #snip }

## 代码清单12-4

Listing 12.4 Gating a client

@@snip[代码清单12-4](../../../../chapter12/src/main/scala/chapter12/GatedStorageClient.scala){ #snip }