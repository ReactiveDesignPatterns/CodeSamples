/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter03.future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ParallelRetrieverExampleTest {
  final Customer customer1 =
      new Customer() {
        @Override
        public long getId() {
          return 1234L;
        }

        @Override
        public String getName() {
          return "Jane Doe";
        }

        @Override
        public String getAddress() {
          return "111 Somewhere St., Somewhereville, NY 10001";
        }

        @Override
        public String getPhone() {
          return "212-555-1212";
        }
      };

  final Customer customer2 =
      new Customer() {
        @Override
        public long getId() {
          return 5678L;
        }

        @Override
        public String getName() {
          return "John Doe";
        }

        @Override
        public String getAddress() {
          return "555 Nowhere St., Nowhereville, NY 10001";
        }

        @Override
        public String getPhone() {
          return "212-555-1212";
        }
      };

  final CacheRetriever workingCacheRetriever = customerId -> customer1;

  final CacheRetriever delayedCacheRetriever =
      customerId -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return null;
      };

  final DBRetriever workingDbRetriever = customerId -> customer2;

  final DBRetriever delayedDbRetriever =
      customerId -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return null;
      };

  @Test
  public void testCacheReturn() {
    ParallelRetrievalExample retreiver =
        new ParallelRetrievalExample(workingCacheRetriever, delayedDbRetriever);
    Object retrievedCustomer = retreiver.retrieveCustomer(1234);
    org.junit.Assert.assertNotEquals(retrievedCustomer, null);
    // org.junit.Assert.assertEquals(retrievedCustomer.getId(), "1234");
  }

  @Test
  public void testDbReturn() {
    ParallelRetrievalExample retreiver =
        new ParallelRetrievalExample(delayedCacheRetriever, workingDbRetriever);
    Object retrievedCustomer = retreiver.retrieveCustomer(5678);
    org.junit.Assert.assertNotEquals(retrievedCustomer, null);
  }
}
