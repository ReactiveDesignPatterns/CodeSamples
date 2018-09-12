/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
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
