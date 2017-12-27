package org.reactivedesignpatterns.chapter2.future;

import java.util.concurrent.CompletableFuture;

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