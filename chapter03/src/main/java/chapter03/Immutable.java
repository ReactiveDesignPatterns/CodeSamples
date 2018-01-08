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

package chapter03;

// Listing 3.2 Immutable message class that behaves predictably and is easier to reason about

// #snip
import java.util.Date;

public class Immutable {
    private final Date timestamp;
    private final String message;
    public Immutable(final Date timestamp, final String message) {
        this.timestamp = new Date(timestamp.getTime());
        this.message = message;
    }
    public Date getTimestamp() {
        return new Date(timestamp.getTime());
    }
    public String getMessage() {
        return message;
    }
}
// #snip
