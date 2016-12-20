/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter14;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import akka.japi.Pair;
import akka.japi.function.*;
import akka.stream.javadsl.*;
import akka.stream.scaladsl.Keep;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

public interface ComplexCommand {
	
	public class DataElement {
		public final int value;

		public DataElement(int value) {
			this.value = value;
		}
	}
	public interface PartialResult {}

	public interface Result {}
	
	public class PartSuccess implements PartialResult {
		public final int value;

		public PartSuccess(int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "ResultSuccess(" + value + ")";
		}
	}

	public class PartFailure implements PartialResult {
		public final Throwable failure;

		public PartFailure(Throwable failure) {
			this.failure = failure;
		}
		
		@Override
		public String toString() {
			return "ResultFailure(" + failure.getMessage() + ")";
		}
	}
	
	public interface ProcessingLogic {
		public PartialResult process(Stream<DataElement> input);
	}
	
	public interface MergeLogic {
		public Result merge(Collection<PartialResult> partialResults);
	}

	public class BatchJob {
		public final String dataSelector;
		public final ProcessingLogic processingLogic;
		public final MergeLogic mergeLogic;
		
		public BatchJob(String dataSelector, ProcessingLogic processingLogic, MergeLogic mergeLogic) {
			this.dataSelector = dataSelector;
			this.processingLogic = processingLogic;
			this.mergeLogic = mergeLogic;
		}
		
		public BatchJob withDataSelector(String selector) {
			return new BatchJob(selector, processingLogic, mergeLogic);
		}
	}
	
	public class BatchJobJS {
		public final String dataSelector;
		public final String processingLogic;
		public final String mergeLogic;

		public BatchJobJS(String dataSelector, String processingLogic, String mergeLogic) {
			this.dataSelector = dataSelector;
			this.processingLogic = processingLogic;
			this.mergeLogic = mergeLogic;
		}
		
		public BatchJobJS withDataSelector(String selector) {
			return new BatchJobJS(selector, processingLogic, mergeLogic);
		}
	}
	
	public class WorkerJS {
		public PartialResult runJob(BatchJobJS job) {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			Invocable invocable = (Invocable) engine;
			try {
				engine.eval(job.processingLogic);
				final Stream<DataElement> input = provideData(job.dataSelector);
				PartialResult result = (PartialResult) invocable.invokeFunction("process", input);
				return result;
			} catch (Exception e) {
				return new PartFailure(e);
			}
		}
		private Stream<DataElement> provideData(String selector) {
			/* fetch data from persistent storage in streaming fashion */
			return Stream.of(1, 2, 3).map(DataElement::new);
		}
	}
	
	class InRange implements Predicate<DataElement> {
		private static final long serialVersionUID = 1L;
		public final String fieldname;
		public final Number min;
		public final Number max;
		public InRange(String fieldname, Number min, Number max) {
			this.fieldname = fieldname;
			this.min = min;
			this.max = max;
		}
		@Override
		public boolean test(DataElement arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	class Median<T> implements Function2<T, DataElement, T> {
		private static final long serialVersionUID = 1L;
		public final String fieldname;

		public Median(String fieldname) {
			this.fieldname = fieldname;
		}

		@Override
		public T apply(T arg0, DataElement arg1) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	class Inject<T> implements Function<DataElement, DataElement> {
		private static final long serialVersionUID = 1L;
		public final RunnableGraph<Future<T>> value;
		public final String fieldname;

		public Inject(RunnableGraph<Future<T>> value, String fieldname) {
			this.value = value;
			this.fieldname = fieldname;
		}

		@Override
		public DataElement apply(DataElement arg0) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	class Filter implements Predicate<DataElement> {
		private static final long serialVersionUID = 1L;
		public final String expression;

		public Filter(String expression) {
			this.expression = expression;
		}

		@Override
		public boolean test(DataElement arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	class DistinctValues<T> implements Function2<Set<T>, DataElement, Set<T>> {
		private static final long serialVersionUID = 1L;
		public final String[] fields;

		public DistinctValues(String... fields) {
			this.fields = fields;
		}

		@Override
		public Set<T> apply(Set<T> arg0, DataElement arg1) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static void akkaStreamDSL() {
		RunnableGraph<Future<Long>> p =
			Source.<DataElement>empty()
			.filter(new InRange("year", 1950, 1960))
			.toMat(Sink.fold(0L, new Median<Long>("price")), Keep.<BoxedUnit, Long>right());
		
		Source.<DataElement>empty()
			.map(new Inject<Long>(p, "p"))
			.filter(new Filter("price > p"))
			.to(Sink.fold(Collections.emptySet(), new DistinctValues<Pair<String, String>>("make", "model")));
	}
}
