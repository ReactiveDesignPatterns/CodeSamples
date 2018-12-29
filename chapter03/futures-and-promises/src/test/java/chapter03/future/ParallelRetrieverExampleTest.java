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
  private final Customer customer1 =
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
          return "111 Somewhere St., SomewhereVille, NY 10001";
        }

        @Override
        public String getPhone() {
          return "212-555-1212";
        }
      };

    private final Customer customer2 =
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
          return "555 Nowhere St., NowhereVille, NY 10001";
        }

        @Override
        public String getPhone() {
          return "212-555-1212";
        }
      };

    private final CacheRetriever workingCacheRetriever = customerId -> customer1;

    private final CacheRetriever delayedCacheRetriever =
      customerId -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return null;
      };

    private final DBRetriever workingDbRetriever = customerId -> customer2;

    private final DBRetriever delayedDbRetriever =
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
    ParallelRetrievalExample retriever =
        new ParallelRetrievalExample(workingCacheRetriever, delayedDbRetriever);
    Object retrievedCustomer = retriever.retrieveCustomer(1234);
    org.junit.Assert.assertNotEquals(retrievedCustomer, null);
    // org.junit.Assert.assertEquals(retrievedCustomer.getId(), "1234");
  }

  @Test
  public void testDbReturn() {
    ParallelRetrievalExample retriever =
        new ParallelRetrievalExample(delayedCacheRetriever, workingDbRetriever);
    Object retrievedCustomer = retriever.retrieveCustomer(5678);
    org.junit.Assert.assertNotEquals(retrievedCustomer, null);
  }
}
