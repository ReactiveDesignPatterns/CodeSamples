package chapter03.future

import java.util.concurrent.ForkJoinPool

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.Try

@RunWith(classOf[JUnit4])
class ParallelRetrievalPromiseExampleTest {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool())
  implicit val timeout: FiniteDuration = 250 milliseconds

  val customer1: Customer {
    def getId: Long

    def getPhone: String

    def getAddress: String

    def getName: String
  } = new Customer() {
    override def getId = 1234L
    override def getName = "Jane Doe"
    override def getAddress = "111 Somewhere St., Somewhereville, NY 10001"
    override def getPhone = "212-555-1212"
  }

  val customer2: Customer {
    def getId: Long

    def getPhone: String

    def getAddress: String

    def getName: String
  } = new Customer() {
    override def getId = 5678L
    override def getName = "John Doe"
    override def getAddress = "555 Nowhere St., Nowhereville, NY 10001"
    override def getPhone = "212-555-1212"
  }

  val workingCacheRetriever: CacheRetriever {
    def getCustomer(customerId: Long): Customer with Object {
      def getId: Long

      def getPhone: String

      def getAddress: String

      def getName: String
    }
  } = new CacheRetriever() {
    override def getCustomer(customerId: Long): Customer {
      def getId: Long

      def getPhone: String

      def getAddress: String

      def getName: String
    } = customer1
  }

  val delayedCacheRetriever: CacheRetriever {
    def getCustomer(customerId: Long): Null
  } = new CacheRetriever() {
    override def getCustomer(customerId: Long): Null = {
      Try(Thread.sleep(1000))
      null
    }
  }

  val workingDbRetriever: DBRetriever {
    def getCustomer(customerId: Long): Customer with Object {
      def getId: Long

      def getPhone: String

      def getAddress: String

      def getName: String
    }
  } = new DBRetriever() {
    override def getCustomer(customerId: Long): Customer {
      def getId: Long

      def getPhone: String

      def getAddress: String

      def getName: String
    } = customer2
  }

  val delayedDbRetriever: DBRetriever {
    def getCustomer(customerId: Long): Null
  } = new DBRetriever() {
    override def getCustomer(customerId: Long): Null = {
      Try(Thread.sleep(1000))
      null
    }
  }

  @Test
  def testCacheReturn: Unit = {
    val retreiver = new ParallelRetrievalPromiseExample(
      workingCacheRetriever, delayedDbRetriever)
    val retrievedCustomer = retreiver.retrieveCustomer(1234)
    org.junit.Assert.assertNotEquals(retrievedCustomer, null)
  }

  @Test
  def testDbReturn(): Unit = {
    val retreiver = new ParallelRetrievalPromiseExample(
      delayedCacheRetriever, workingDbRetriever);
    val retrievedCustomer = retreiver.retrieveCustomer(5678);
    org.junit.Assert.assertNotEquals(retrievedCustomer, null);
  }
}