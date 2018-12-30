/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import static akka.pattern.Patterns.ask;

public class AskActorWithJava8 {
  public static class Request {
    private final int reqId;

    public Request(int reqId) {
      this.reqId = reqId;
    }

    public int getReqId() {
      return reqId;
    }
  }

  public static class Response {}

  public static class MyActor extends AbstractActor {
    @Override
    public Receive createReceive() {
      return receiveBuilder()
          .matchAny((msg) -> getSender().tell(new Response(), getSelf()))
          .build();
    }
  }

  public static void processIt(Response response) {
    System.out.println(response);
  }

  private static final ActorSystem ACTOR_SYSTEM = ActorSystem.create();

  public static void main(String[] args) {
    final ActorRef actorRef =
        ACTOR_SYSTEM.actorOf(Props.create(MyActor.class, (Creator<MyActor>) MyActor::new));
    final Request request = new Request(1);
    final Duration timeout = Duration.ofSeconds(1L);

    // #snip
    final CompletionStage<Response> future =
        ask(actorRef, request, timeout).thenApply(Response.class::cast);
    future.thenAccept(AskActorWithJava8::processIt);
    // #snip

  }
}
