# 第15章——消息流模式

## 代码清单15-1

Listing 15.1 Server responding to the address that originated the request

@@snip[代码清单15-1](../../../../chapter15/src/main/java/chapter15/Server.java){ #snip }

## 代码清单15-2

Listing 15.2 Client sending a request and then blocking until the server responds

@@snip[代码清单15-2](../../../../chapter15/src/main/java/chapter15/Client.java){ #snip }

*Server 的输出结果*

@@snip[代码清单15-1](../../../../chapter15/src/main/output/server.output){ #snip }

*Client 的可能输出结果*

@@snip[代码清单15-2](../../../../chapter15/src/main/output/client.output){ #snip }

*HTTP Request Header*

@@snip[HTTP Request Header](../../../../chapter15/src/main/output/http-request.header){ #snip }

*HTTP Response Header*

@@snip[HTTP Response Header](../../../../chapter15/src/main/output/http-response.header){ #snip }

## 代码清单15-3

Listing 15.3 Untyped Akka Actors modeling request–response

@@snip[代码清单15-3](../../../../chapter15/src/main/scala/chapter15/RequestResponseActors.scala){ #snip }

*运行结果*

@@snip[代码清单15-3](../../../../chapter15/src/main/output/request-response-actors.output){ #snip }

## 代码清单15-4

Listing 15.4 Including the response explicitly in the request message

@@snip[代码清单15-4](../../../../chapter15/src/main/scala/chapter15/RequestResponseTypedActors.scala){ #snip }

## 代码清单15-5

Listing 15.5 Request–response based on a one-way messaging protocol

@@snip[代码清单15-5](../../../../chapter15/src/main/js/request-response.js){ #snip }

## 代码清单15-6

Listing 15.6 Listening for a response with the same correlation ID as the original request

@@snip[代码清单15-6](../../../../chapter15/src/main/js/request-response-2way.js){ #snip }

## SMTP

@@snip[SMTP](../../../../chapter15/src/main/output/smtp.protocal){ #snip }

## 代码清单15-7

Listing 15.7 Encapsulated information needed for multiple SMTP exchanges

@@snip[代码清单15-7](../../../../chapter15/src/main/scala/chapter15/SendEmail.scala){ #snip }

@@snip[代码清单15-7](../../../../chapter15/src/main/scala/chapter15/Result.scala){ #snip }

## 代码清单15-8

Listing 15.8 Separating the body so it can be delivered on demand

@@snip[代码清单15-8](../../../../chapter15/src/main/scala/chapter15/streamed/SendEmail.scala){ #snip }

## 代码清单15-9

Listing 15.9 Enabling the body to be pulled by the recipient

@@snip[代码清单15-9](../../../../chapter15/src/main/scala/chapter15/pullable/SendEmail.scala){ #snip }