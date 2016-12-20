/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter14;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scala.PartialFunction;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.dispatch.Futures;
import akka.japi.pf.PFBuilder;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.CircuitBreaker;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/**
 * This is not a completely runnable example but only a fully compiled
 * collection of code snippets used in section 13.1.
 */
public class ResourceEncapsulation {
	public Instance startInstance(AWSCredentials credentials) {
		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

		RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
				.withImageId("").withInstanceType("m1.small").withMinCount(1)
				.withMaxCount(1);

		RunInstancesResult runInstancesResult = amazonEC2Client
				.runInstances(runInstancesRequest);

		Reservation reservation = runInstancesResult.getReservation();
		List<Instance> instances = reservation.getInstances();

		// there will be exactly one instance in this list, otherwise
		// runInstances() would have thrown an exception
		return instances.get(0);
	}

	private ExecutionContext executionContext;
	private CircuitBreaker circuitBreaker;

	public Future<Instance> startInstanceAsync(AWSCredentials credentials) {
		Future<Instance> f = circuitBreaker.callWithCircuitBreaker(() ->
			Futures.future(() -> startInstance(credentials), executionContext));
		PartialFunction<Throwable, Future<Instance>> recovery =
			new PFBuilder<Throwable, Future<Instance>>()
				.match(AmazonClientException.class,
					   ex -> ex.isRetryable(),
					   ex -> startInstanceAsync(credentials))
				.build();
		return f.recoverWith(recovery, executionContext);
	}
	
	public Future<RunInstancesResult> runInstancesAsync(RunInstancesRequest request, AmazonEC2Async client) {
		Promise<RunInstancesResult> promise = Futures.promise();
		client.runInstancesAsync(request, new AsyncHandler<RunInstancesRequest, RunInstancesResult>() {
			@Override
			public void onSuccess(RunInstancesRequest request, RunInstancesResult result) {
				promise.success(result);
			}
			
			@Override
			public void onError(Exception exception) {
				promise.failure(exception);
			}
		});
		return promise.future();
	}
	
	public Future<TerminateInstancesResult> terminateInstancesAsync(AmazonEC2Client client, Instance... instances) {
		List<String> ids = Arrays.stream(instances).map(i -> i.getInstanceId()).collect(Collectors.toList());
		TerminateInstancesRequest request = new TerminateInstancesRequest(ids);
		Future<TerminateInstancesResult> f = circuitBreaker.callWithCircuitBreaker(() ->
			Futures.future(() -> client.terminateInstances(request), executionContext));
		PartialFunction<Throwable, Future<TerminateInstancesResult>> recovery =
			new PFBuilder<Throwable, Future<TerminateInstancesResult>>()
				.match(AmazonClientException.class,
					   ex -> ex.isRetryable(),
					   ex -> terminateInstancesAsync(client, instances))
				.build();
		return f.recoverWith(recovery, executionContext);
	}
	
	static interface WorkerNodeMessage {
		public long id();
		public ActorRef replyTo();
	}
	static class WorkerCommandFailed {
		public final String reason;
		public final long id;
		public WorkerCommandFailed(String reason, long id) {
			this.reason = reason;
			this.id = id;
		}
	}
	static class DoHealthCheck {
		static public DoHealthCheck instance = new DoHealthCheck();
	}
	static class Shutdown {
		static public Shutdown instance = new Shutdown();
	}
	static class WorkerNodeReady {
		static public WorkerNodeReady instance = new WorkerNodeReady();
	}
	
	class WorkerNode extends AbstractActor {
		private final Cancellable checkTimer;
		
		public WorkerNode(InetAddress address, FiniteDuration checkInterval) {
			checkTimer = getContext().system().scheduler().schedule(checkInterval, checkInterval, self(), DoHealthCheck.instance, getContext().dispatcher(), self());
			
			List<WorkerNodeMessage> msgs = new ArrayList<>();
			receive(ReceiveBuilder
					.match(WorkerNodeMessage.class, msgs::add)
					.match(DoHealthCheck.class, dhc -> { /* perform check */ })
					.match(Shutdown.class, s -> {
						msgs.stream().forEach(msg -> msg.replyTo().tell(new WorkerCommandFailed("shutting down", msg.id()), self()));
						/* ask Resource Pool to shut down this instance */
					})
					.match(WorkerNodeReady.class, wnr -> {
						/* send msgs to the worker */
						getContext().become(initialized());
					})
					.build());
		}
		
		private PartialFunction<Object, BoxedUnit> initialized() {
			/* forward commands and deal with responses from worker node */
			return null;
		}
		
		@Override
		public void postStop() {
			checkTimer.cancel();
		}
	}

}
