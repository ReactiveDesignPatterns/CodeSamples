/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

package chapter14;

import chapter14.ComplexCommand.BatchJobJS;
import chapter14.ComplexCommand.PartialResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ComplexCommandTest {
  public static void main(String[] args) throws IOException {
    try (InputStream js =
        ComplexCommandTest.class
            .getResourceAsStream("/chapter14/job.js");
        Scanner s = new Scanner(js, "UTF-8")) {
      s.useDelimiter("\\A");
      final BatchJobJS job = new BatchJobJS("", s.next(), "");
      final ComplexCommand.WorkerJS worker = new ComplexCommand.WorkerJS();
      final PartialResult result = worker.runJob(job);
      System.out.println(result);
    }
  }
}
