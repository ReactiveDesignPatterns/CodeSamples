/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03

//代码清单 3-3 使用Scala的case class定义的不可变消息

// #snip
import java.util.Date

final case class Message(timestamp: Date, message: String)

// #snip
