package com.reactivedesignpatterns.chapter14;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.reactivedesignpatterns.chapter14.ComplexCommand.*;

public class ComplexCommandTest {
	public static void main(String[] args) throws IOException {
		try (InputStream js = ComplexCommandTest.class.getResourceAsStream("/com/reactivedesignpatterns/chapter14/job.js");
				Scanner s = new Scanner(js, "UTF-8")) {
			s.useDelimiter("\\A");
			final BatchJobJS job = new BatchJobJS("", s.next(), "");
			final WorkerJS worker = new WorkerJS();
			final PartialResult result = worker.runJob(job);
			System.out.println(result);
		}
	}
}
