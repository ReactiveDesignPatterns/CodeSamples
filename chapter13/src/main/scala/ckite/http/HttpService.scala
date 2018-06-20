/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package ckite.http

import ckite.{ CKite, CKiteClient, Get, Put }
import com.fasterxml.jackson.core.util.{ DefaultIndenter, DefaultPrettyPrinter }
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.Version.Http11
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.{ Method, Request, Response }
import com.twitter.util.{ Future, Promise }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future ⇒ ScalaFuture }
import scala.language.implicitConversions
import scala.util.{ Failure, Success }

class HttpService(ckite: CKite) extends Service[Request, Response] {

  private val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)
  private val printer = new DefaultPrettyPrinter
  printer.indentArraysWith(new DefaultIndenter)
  private val writer = mapper.writer(printer)

  def apply(request: Request): Future[Response] = {
    request.method -> Path(request.path) match {
      case Method.Get -> Root / "status" ⇒ Future.value {
        response(writer.writeValueAsString(ckite.asInstanceOf[CKiteClient].stats()))
      }
      case Method.Get -> Root / "kv" / key ⇒
        val localOption = request.params.getBoolean("local")
        val get = Get(key)
        val result = if (localOption.getOrElse(false))
          ScalaFuture.successful(ckite.asInstanceOf[CKiteClient].readLocal(get))
        else ckite.read(get)
        result.map { value ⇒ response(value) }
      case Method.Post -> Root / "kv" / key / value ⇒
        ckite.write(Put(key, value)) map { value ⇒ response(value) }
      case Method.Post -> Root / "members" / binding ⇒
        ckite.addMember(binding) map { value ⇒ response(value) }
      case Method.Delete -> Root / "members" / binding ⇒
        ckite.removeMember(binding) map { value ⇒ response(value) }
      case _ ⇒
        Future value Response(Http11, NotFound)
    }
  }

  private def response[T](any: T): Response = {
    val response = Response()
    response.contentString = s"$any\n"
    response
  }

  private implicit def toTwitterFuture[T](scalaFuture: ScalaFuture[T]): Future[T] = {
    val promise = Promise[T]()
    scalaFuture.onComplete {
      case Success(value) ⇒ promise.setValue(value)
      case Failure(t)     ⇒ promise.raise(t)
    }
    promise
  }
}
