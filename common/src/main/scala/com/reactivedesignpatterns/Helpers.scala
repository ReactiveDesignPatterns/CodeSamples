/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package com.reactivedesignpatterns

object Helpers {

  def isCiTest: Boolean = sys.env.get("TRAVIS").contains("true")

}
