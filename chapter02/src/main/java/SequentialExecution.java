/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

public class SequentialExecution {
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

  public static ReplyA computeA() {
    return new ReplyA(); // return from compute
  }

  public static ReplyB computeB() {
    return new ReplyB(); //return from compute
  }

  public static ReplyC computeC() {
    return new ReplyC(); //return from compute
  }

  public static void main(String[] args) {
    // #snip
    ReplyA a = computeA();
    ReplyB b = computeB();
    ReplyC c = computeC();

    Result r = aggregate(a, b, c);
    // #snip

    System.out.println(r);
  }

}
