/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

import reactivedesignpatterns.NamedPoolThreadFactory;

import java.util.concurrent.*;

public class ParallelExecutionWithJavaFuture {
  public static class ReplyA {}

  public static class ReplyB {}

  public static class ReplyC {}

  public static class Result {
    final ReplyA replyA;
    final ReplyB replyB;
    final ReplyC replyC;

    public Result(ReplyA replyA, ReplyB replyB, ReplyC replyC) {
      this.replyA = replyA;
      this.replyB = replyB;
      this.replyC = replyC;
    }
  }

  public static Result aggregate(ReplyA replyA, ReplyB replyB, ReplyC replyC) {
    return new Result(replyA, replyB, replyC);
  }

  private static final ExecutorService EXECUTOR_SERVICE =
      new ThreadPoolExecutor(
          3,
          3,
          60,
          TimeUnit.SECONDS,
          new LinkedBlockingDeque<>(),
          new NamedPoolThreadFactory("Parallelism", true),
          new ThreadPoolExecutor.CallerRunsPolicy());

  public static Future<ReplyA> taskA() {
    return EXECUTOR_SERVICE.submit(ReplyA::new); // return from compute
  }

  public static Future<ReplyB> taskB() {
    return EXECUTOR_SERVICE.submit(ReplyB::new); // return from compute
  }

  public static Future<ReplyC> taskC() {
    return EXECUTOR_SERVICE.submit(ReplyC::new); // return from compute
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
