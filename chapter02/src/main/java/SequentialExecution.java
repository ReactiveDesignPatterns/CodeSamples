/*
 * Copyright 2018 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
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
