/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter14;

import akka.japi.Pair;
import akka.japi.function.Function;
import akka.japi.function.Function2;
import akka.japi.function.Predicate;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface ComplexCommand {

  public class DataElement {
    public final int value;

    public DataElement(int value) {
      this.value = value;
    }
  }

  public interface PartialResult {}

  public interface Result {}

  // #snip_14-8
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
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    public PartialResult runJob(BatchJobJS job) {
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
  // #snip_14-8

  // #snip_14-7
  public interface ProcessingLogic {
    PartialResult process(Stream<DataElement> input);
  }

  //
  public interface MergeLogic {
    Result merge(Collection<PartialResult> partialResults);
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
  // #snip_14-7

  public class InRange implements Predicate<DataElement> {
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

  public class Median<T> implements Function2<T, DataElement, T> {
    private static final long serialVersionUID = 1L;
    public final String fieldname;

    public Median(String fieldname) {
      this.fieldname = fieldname;
    }

    @Override
    public T apply(T arg0, DataElement arg1) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  public class Inject<T> implements Function<DataElement, DataElement> {
    private static final long serialVersionUID = 1L;
    public final RunnableGraph<CompletionStage<T>> value;
    public final String fieldname;

    public Inject(RunnableGraph<CompletionStage<T>> value, String fieldname) {
      this.value = value;
      this.fieldname = fieldname;
    }

    @Override
    public DataElement apply(DataElement arg0) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  public class Filter implements Predicate<DataElement> {
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

  public class DistinctValues<T> implements Function2<Set<T>, DataElement, Set<T>> {
    private static final long serialVersionUID = 1L;
    public final String[] fields;

    public DistinctValues(String... fields) {
      this.fields = fields;
    }

    @Override
    public Set<T> apply(Set<T> arg0, DataElement arg1) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  // #snip_14-10
  public static void akkaStreamDSL() {
    RunnableGraph<CompletionStage<Long>> p =
        Source.<DataElement>empty()
            .filter(new InRange("year", 1950, 1960))
            .toMat(Sink.fold(0L, new Median<Long>("price")), Keep.right());

    Source.<DataElement>empty()
        .map(new Inject<Long>(p, "p"))
        .filter(new Filter("price > p"))
        .to(
            Sink.fold(
                Collections.emptySet(), new DistinctValues<Pair<String, String>>("make", "model")));
  }
  // #snip_14-10
}
