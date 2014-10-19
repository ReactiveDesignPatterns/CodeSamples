package org.reactivedesignpatterns.chapter2.future

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.ForkJoinPool
import org.junit.runner.RunWith
import scala.util.Try
import org.junit.runner.RunWith

@RunWith(classOf[JUnit4])
class ParallelRetrievalPromiseExampleTest {
  implicit val ec = ExecutionContext.fromExecutor(new ForkJoinPool())
  implicit val timeout = 250 milliseconds

  val customer1 = new Customer() {
    override def getId = 1234L
    override def getName = "Jane Doe"
    override def getAddress = "111 Somewhere St., Somewhereville, NY 10001"
    override def getPhone = "212-555-1212"
  }

  val customer2 = new Customer() {
    override def getId = 5678L
    override def getName = "John Doe"
    override def getAddress = "555 Nowhere St., Nowhereville, NY 10001"
    override def getPhone = "212-555-1212"
  }

  val workingCacheRetriever = new CacheRetriever() {
    override def getCustomer(customerId: Long) = customer1
  }

  val delayedCacheRetriever = new CacheRetriever() {
    override def getCustomer(customerId: Long) = {
      Try(Thread.sleep(1000))
      null
    }
  }

  val workingDbRetriever = new DBRetriever() {
    override def getCustomer(customerId: Long) = customer2
  }

  val delayedDbRetriever = new DBRetriever() {
    override def getCustomer(customerId: Long) = {
      Try(Thread.sleep(1000))
      null
    }
  }

  @Test
  def testCacheReturn = {
    val retreiver = new ParallelRetrievalPromiseExample(
      workingCacheRetriever, delayedDbRetriever)
    val retrievedCustomer = retreiver.retrieveCustomer(1234)
    org.junit.Assert.assertNotEquals(retrievedCustomer, null)
  }

  @Test
  def testDbReturn() = {
    val retreiver = new ParallelRetrievalPromiseExample(
      delayedCacheRetriever, workingDbRetriever);
    val retrievedCustomer = retreiver.retrieveCustomer(5678);
    org.junit.Assert.assertNotEquals(retrievedCustomer, null);
  }
}