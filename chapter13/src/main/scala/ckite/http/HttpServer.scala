/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package ckite.http

import java.net.InetSocketAddress

import ckite.CKite
import com.twitter.finagle.Http
import com.twitter.util.Closable
import com.typesafe.config.ConfigFactory

class HttpServer(ckite: CKite) {

  var closed = false
  var server: Closable = _

  def start() = {
    val restServerPort =
      ConfigFactory.load()
        .getString("ckite.listen-address").split(":")(1).toInt + 1000

    val adminServerPort = restServerPort + 1000
    server =
      Http.serve(new InetSocketAddress(restServerPort), new HttpService(ckite))
  }

  def stop() = synchronized {
    if (!closed) {
      server.close()
      closed = true
    }
  }

}

object HttpServer {
  def apply(ckite: CKite) = new HttpServer(ckite)
}
