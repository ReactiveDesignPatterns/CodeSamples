/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
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