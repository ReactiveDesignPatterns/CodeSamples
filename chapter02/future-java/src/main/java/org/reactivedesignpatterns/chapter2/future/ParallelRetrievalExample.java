package org.reactivedesignpatterns.chapter2.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ParallelRetrievalExample {
    final CacheRetriever cacheRetriever;
    final DBRetriever dbRetriever;

    ParallelRetrievalExample(CacheRetriever cacheRetriever,
            DBRetriever dbRetriever) {
        this.cacheRetriever = cacheRetriever;
        this.dbRetriever = dbRetriever;
    }

    public Object retrieveCustomer(final long id) {
        final CompletableFuture<Object> cacheFuture = CompletableFuture
                .supplyAsync(() -> {
                    return cacheRetriever.getCustomer(id);
                });
        final CompletableFuture<Object> dbFuture = CompletableFuture
                .supplyAsync(() -> {
                    return dbRetriever.getCustomer(id);
                });

        return CompletableFuture.anyOf(cacheFuture, dbFuture);
    }
}