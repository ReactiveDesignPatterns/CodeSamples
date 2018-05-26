/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15

sealed trait StatusCode

object StatusCode {

  case object OK extends StatusCode

  case object Failed extends StatusCode

}
