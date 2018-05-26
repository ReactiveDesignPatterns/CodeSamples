/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

package chapter14;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import chapter14.ResourceEncapsulation.*;
import chapter14.ResourceEncapsulation.Shutdown;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoan {

  // #snip_14-6
  class WorkerNodeForExecution extends AbstractActor {

    @Override
    public Receive createReceive() {
      List<WorkerNodeMessage> msgs = new ArrayList<>();
      return receiveBuilder()
        .match(WorkerNodeMessage.class, msgs::add)
        .match(Shutdown.class, s -> {
          msgs.stream().forEach(msg -> {
            WorkerCommandFailed failMsg =
              new WorkerCommandFailed("shutting down", msg.id());
            msg.replyTo().tell(failMsg, self());
          });
        })
        .match(WorkerNodeReady.class, wnr -> {
          getContext().become(initialized());
        })
        .build();
    }

    private PartialFunction<Object, BoxedUnit> initialized() {
      /* forward commands and deal with responses from worker node */
      return null;
    }
  }

  class WorkNodeForResourcePool extends AbstractActor {
    private final Cancellable checkTimer;

    public WorkNodeForResourcePool(InetAddress address,
        FiniteDuration checkInterval) {
      checkTimer = getContext().system().scheduler()
          .schedule(checkInterval, checkInterval, self(),
              DoHealthCheck.instance, getContext().dispatcher(), self());

    }

    @Override
    public Receive createReceive() {
      return receiveBuilder()
        .match(DoHealthCheck.class, dhc -> { /* perform check */ })
        .match(Shutdown.class, s -> {/* Cleans up this resource */})
        .build();
    }

    @Override
    public void postStop() {
      checkTimer.cancel();
    }
  }
  // #snip_14-6

}
