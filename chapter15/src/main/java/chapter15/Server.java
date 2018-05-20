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

package chapter15;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

// 代码清单15-1
// Listing 15.1 Server responding to the address that originated the request

// #snip
public class Server {
  static final int SERVER_PORT = 8888;

  static public void main(String[] args) throws IOException {
    // bind a socket for receiving packets
    try (final DatagramSocket socket =
        new DatagramSocket(SERVER_PORT)) {

      // receive one packet
      final byte[] buffer = new byte[1500];
      final DatagramPacket packet1 =
          new DatagramPacket(buffer, buffer.length);
      socket.receive(packet1);

      final SocketAddress sender = packet1.getSocketAddress();
      System.out.println("server: received " +
          new String(packet1.getData()));
      System.out.println("server: sender was " + sender);

      // send response back
      final byte[] response = "got it!".getBytes();
      final DatagramPacket packet2 =
          new DatagramPacket(response, response.length, sender);
      socket.send(packet2);
    }
  }
}
// #snip
