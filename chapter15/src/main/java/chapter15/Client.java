/*
 * Copyright 2017 https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    static public void main(String[] args) throws IOException {
        // get local socket with random port
        try (final DatagramSocket socket = new DatagramSocket()) {

            // send message to server
            final byte[] request = "hello".getBytes();
            final SocketAddress serverAddress =
                    new InetSocketAddress("localhost", SERVER_PORT);
            final DatagramPacket packet1 =
                    new DatagramPacket(request, request.length, serverAddress);
            socket.send(packet1);

            // receive one packet
            final byte[] buffer = new byte[1500];
            final DatagramPacket packet2 =
                    new DatagramPacket(buffer, buffer.length);
            socket.receive(packet2);

            final SocketAddress sender = packet2.getSocketAddress();
            System.out.println("client: received " +
                    new String(packet2.getData()));
            System.out.println("client: sender was " + sender);
        }
    }
}
// #snip
