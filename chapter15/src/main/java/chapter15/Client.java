/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter15;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import static chapter15.Server.SERVER_PORT;

// 代码清单 15-2
// Listing 15.2 Client sending a request and then blocking until the server responds

// #snip
public class Client {
  public static void main(String[] args) throws IOException {
    // get local socket with random port
    try (final DatagramSocket socket = new DatagramSocket()) {

      // send message to server
      final byte[] request = "hello".getBytes();
      final SocketAddress serverAddress = new InetSocketAddress("localhost", SERVER_PORT);
      final DatagramPacket packet1 = new DatagramPacket(request, request.length, serverAddress);
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
// #snip
