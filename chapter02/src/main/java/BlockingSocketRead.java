/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * 
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 */

import java.io.IOException;
import java.net.Socket;

public class BlockingSocketRead {
  public static void main(String[] args) throws IOException {
    byte[] requestMessageBytes = "request".getBytes();
    byte[] responseBuffer = new byte[1024];

    // #snip
    final Socket socket = new Socket("127.0.0.1", 8080);
    socket.getOutputStream().write(requestMessageBytes);
    final int bytesRead = socket.getInputStream().read(responseBuffer);
    // #snip

  }
}
