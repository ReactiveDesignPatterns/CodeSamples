package org.reactivedesignpatterns.chapter2.future

import java.util.concurrent.ForkJoinPool
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.Try

trait Customer {
  def getId(): Long
  def getName(): String
  def getAddress(): String
  def getPhone(): String
}

trait CacheRetriever {
  def getCustomer(customerId: Long): Customer
}

trait DBRetriever {
  def getCustomer(customerId: Long): Customer
}

class ParallelRetrievalPromiseExample(cacheRetriever: CacheRetriever, dbRetriever: DBRetriever) {
  def retrieveCustomer(id: Long): Future[Customer] = {
    // Import the duration DSL to be used in the timeout
    import scala.concurrent.duration._

    // Set up the thread pool and timeouts
    implicit val ec = ExecutionContext.fromExecutor(new ForkJoinPool())
    implicit val timeout = 250 milliseconds

    // Create the Promise instance that will be used
    val returnCustomerPromise = Promise[Customer]()

    // Create the competing futures
    Future {
      returnCustomerPromise.tryComplete(Try(cacheRetriever.getCustomer(id)))
    }
    Future {
      returnCustomerPromise.tryComplete(Try(dbRetriever.getCustomer(id)))
    }

    // Return the Future instance
    returnCustomerPromise.future
  }
}