package com.reactivedesignpatterns.chapter13;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

import javax.sql.DataSource;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

public interface ManagedBlocking {
	
	public enum AccessRights {
		READ_JOB_STATUS,
		SUBMIT_JOB;
		
		public static final AccessRights[] EMPTY = new AccessRights[] {};
	}

	public class CheckAccess {
		public final String username;
		public final String credentials;
		public final AccessRights[] rights;
		public final ActorRef replyTo;
	
		public CheckAccess(String username, String credentials, AccessRights[] rights, ActorRef replyTo) {
			this.username = username;
			this.credentials = credentials;
			this.rights = rights;
			this.replyTo = replyTo;
		}
	}
	
	public class CheckAccessResult {
		public final String username;
		public final String credentials;
		public final AccessRights[] rights;

		public CheckAccessResult(String username, String credentials, AccessRights[] rights) {
			this.username = username;
			this.credentials = credentials;
			this.rights = rights;
		}
	}
	
	public class AccessService extends AbstractActor {
		private Executor pool;
		
		public AccessService(DataSource db, int poolSize, int queueSize) {
			pool = new ThreadPoolExecutor(0, poolSize, 60, SECONDS, new LinkedBlockingDeque<>(queueSize));

			final ActorRef self = self();
			receive(ReceiveBuilder
				.match(CheckAccess.class, ca -> {
					try {
						pool.execute(() -> checkAccess(db, ca, self));
					} catch (RejectedExecutionException e) {
						ca.replyTo.tell(new CheckAccessResult(ca.username, ca.credentials, AccessRights.EMPTY), self);
					}})
				.build());
		}
		
		private static void checkAccess(DataSource db, CheckAccess ca, ActorRef self) {
			try {
				final Connection conn = db.getConnection();
				final ResultSet result = conn.createStatement().executeQuery("<figure out access rights>");
				final List<AccessRights> rights = new LinkedList<>();
				while (result.next()) {
					rights.add(AccessRights.valueOf(result.getString(0)));
				}
				ca.replyTo.tell(new CheckAccessResult(ca.username, ca.credentials, rights.toArray(AccessRights.EMPTY)), self);
			} catch (Exception e) {
				ca.replyTo.tell(new CheckAccessResult(ca.username, ca.credentials, AccessRights.EMPTY), self);
			}
		}
	}
	
}
