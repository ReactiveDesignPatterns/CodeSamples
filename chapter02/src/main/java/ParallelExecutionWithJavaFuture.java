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

  public static Future<ReplyA> taskA(){
        return EXECUTOR_SERVICE.submit(ReplyA::new); // return from compute
    }

  public static Future<ReplyB> taskB(){
        return EXECUTOR_SERVICE.submit(ReplyB::new); //return from compute
    }

  public static Future<ReplyC> taskC(){
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
