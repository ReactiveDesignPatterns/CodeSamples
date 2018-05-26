/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelExecutionWithJavaFuture {
  public static class ReplyA {
  }

  public static class ReplyB {
  }

  public static class ReplyC {
  }

  public static class Result {
    ReplyA replyA;
    ReplyB replyB;
    ReplyC replyC;

    public Result(ReplyA replyA, ReplyB replyB, ReplyC replyC) {
      this.replyA = replyA;
      this.replyB = replyB;
      this.replyC = replyC;
    }
  }

  public static Result aggregate(ReplyA replyA, ReplyB replyB, ReplyC replyC) {
    return new Result(replyA, replyB, replyC);
  }

  private final static ExecutorService EXECUTOR_SERVICE =
      Executors.newFixedThreadPool(3);

  public static Future<ReplyA> taskA() {
    return EXECUTOR_SERVICE.submit(ReplyA::new); // return from compute
  }

  public static Future<ReplyB> taskB() {
    return EXECUTOR_SERVICE.submit(ReplyB::new); //return from compute
  }

  public static Future<ReplyC> taskC() {
    return EXECUTOR_SERVICE.submit(ReplyC::new); //return from compute
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    // #snip
    Future<ReplyA> a = taskA();
    Future<ReplyB> b = taskB();
    Future<ReplyC> c = taskC();

    Result r = aggregate(a.get(), b.get(), c.get());
    // #snip

    System.out.println(r);
  }

}
