/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter15;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class RequestResponse {
	private static final int SERVER_PORT = 8888;

	static public class Server {
		static public void main(String[] args) throws IOException {
			// bind a socket for receiving packets
			try (final DatagramSocket socket = new DatagramSocket(SERVER_PORT)) {

				// receive one packet
				final byte[] buffer = new byte[1500];
				final DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet1);

				final SocketAddress sender = packet1.getSocketAddress();
				System.out.println("server: received " + new String(packet1.getData()));
				System.out.println("server: sender was " + sender);

				// send response back
				final byte[] response = "got it!".getBytes();
				final DatagramPacket packet2 = new DatagramPacket(response, response.length, sender);
				socket.send(packet2);
			}
		}
	}

	static public class Client {
		static public void main(String[] args) throws IOException {
			// get local socket with random port
			try (final DatagramSocket socket = new DatagramSocket()) {

				// send message to server
				final byte[] request = "hello".getBytes();
				final DatagramPacket packet1 = new DatagramPacket(request, request.length,
						new InetSocketAddress("localhost", SERVER_PORT));
				socket.send(packet1);

				// receive one packet
				final byte[] buffer = new byte[1500];
				final DatagramPacket packet2 = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet2);

				final SocketAddress sender = packet2.getSocketAddress();
				System.out.println("client: received " + new String(packet2.getData()));
				System.out.println("client: sender was " + sender);
			}
		}
	}

}
