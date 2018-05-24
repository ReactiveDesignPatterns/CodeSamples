/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

package chapter14;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import chapter14.ResourceEncapsulation.DoHealthCheck;
import chapter14.ResourceEncapsulation.Shutdown;
import chapter14.ResourceEncapsulation.WorkerCommandFailed;
import chapter14.ResourceEncapsulation.WorkerNodeMessage;
import chapter14.ResourceEncapsulation.WorkerNodeReady;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.opsworks.model.ShutdownEventConfiguration;
import scala.PartialFunction;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
