/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

package chapter03.future;

import java.util.concurrent.CompletableFuture;

// 代码清单3-11
// Listing 3.5 Retrieving the result from the faster source

// #snip
public class ParallelRetrievalExample {
  private final CacheRetriever cacheRetriever;
  private final DBRetriever dbRetriever;

  ParallelRetrievalExample(CacheRetriever cacheRetriever,
      DBRetriever dbRetriever) {
    this.cacheRetriever = cacheRetriever;
    this.dbRetriever = dbRetriever;
  }

  public Object retrieveCustomer(final long id) {
        final CompletableFuture<Object> cacheFuture = CompletableFuture
                .supplyAsync(() -> cacheRetriever.getCustomer(id));
        final CompletableFuture<Object> dbFuture = CompletableFuture
                .supplyAsync(() -> dbRetriever.getCustomer(id));

        return CompletableFuture.anyOf(cacheFuture, dbFuture);
    }
}
// #snip
