/**
 * Copyright (C) 2017 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns

object Helpers {

  def isCiTest: Boolean = sys.env.get("TRAVIS") == Some("true")

}
