package com.reactivedesignpatterns.chapter4

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class TranslationService {
  import ExecutionContext.Implicits.global
  
  def translate(input: String): Future[String] = Future { Thread.sleep(100); "How are you?" }
  
  def translate(input: String, ec: ExecutionContext): Future[String] =
    Future { Thread.sleep(100); "How are you?" }(ec)
}
