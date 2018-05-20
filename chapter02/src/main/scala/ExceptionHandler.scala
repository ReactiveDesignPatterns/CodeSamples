/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

object ExceptionHandler {
  def f(int: Int): Int = int

  def main(args: Array[String]): Unit = {
    val i = 3
    // #snip
    try {
      f(i)
    } catch {
      case _: java.lang.ArithmeticException ⇒ Int.MaxValue
      case ex: java.lang.StackOverflowError ⇒ ???
      case ex: java.net.ConnectException    ⇒ ???
    }
    // #snip

  }
}
