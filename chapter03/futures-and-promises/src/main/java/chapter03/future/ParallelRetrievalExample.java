/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
