package com.reactivedesignpatterns.chapter4

import org.scalatest.WordSpec
import org.scalatest.Matchers
import com.reactivedesignpatterns.Defaults._
import scala.util.Success
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._
import org.scalatest.concurrent.Eventually

class TranslationServiceSpec extends WordSpec with Matchers with Eventually {

  "A TranslationService" should {

    "correctly translate from Swedish" when {

      "using SynchronousEventLoop" in {
        val tr = new TranslationService
        val input = "Hur mår du?"
        val output = "How are you?"
        val ec = SynchronousEventLoop
        tr.translate(input, ec).value.get should be(Success(output))
      }
      
      "using Await.result" in {
        val tr = new TranslationService
        val input = "Hur mår du?"
        val output = "How are you?"
        val future = tr.translate(input)
        Await.result(future, 1.second) should be(output)
      }
      
      "using eventually()" in {
        val tr = new TranslationService
        val input = "Hur mår du?"
        val output = "How are you?"
        val future = tr.translate(input)
        eventually {
          future.value.get should be(Success(output))
        }
      }

    }

    "not respond immediately when running asynchronously" in {
      val tr = new TranslationService
      val input = "Hur mår du?"
      val output = "How are you?"
      val ec = ExecutionContext.global
      val future = tr.translate(input, ec)
      future.value should be(None)
      Await.result(future, 1.second) should be(output)
    }

  }

}