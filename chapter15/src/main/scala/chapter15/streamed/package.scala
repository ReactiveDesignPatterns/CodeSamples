/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15

import akka.NotUsed

package object streamed {
  type Source[T] = akka.stream.scaladsl.Source[T, NotUsed]
}
