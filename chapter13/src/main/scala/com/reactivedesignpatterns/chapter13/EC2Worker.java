/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter13;

import java.util.List;

import scala.PartialFunction;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.dispatch.Futures;
import akka.japi.pf.PFBuilder;
import akka.pattern.CircuitBreaker;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class EC2Worker {
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

}
