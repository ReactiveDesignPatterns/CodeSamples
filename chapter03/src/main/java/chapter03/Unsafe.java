/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package chapter03;


// Listing 3.1 Unsafe, mutable message class, which may hide unexpected behavior
// #snip
import java.util.Date;

public class Unsafe {
  private Date timestamp;
  private final StringBuffer message;

  public Unsafe(Date timestamp, StringBuffer message) {
    this.timestamp = timestamp;
    this.message = message;
  }

  public synchronized Date getTimestamp() {
    return timestamp;
  }

  public synchronized void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public StringBuffer getMessage() {
    return message;
  }
}
// #snip