/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter13

import play.api.libs.json.JsValue
import java.io.File
import sbt.IO
import play.api.libs.json.Json
import akka.actor.ActorRef

object ReplicationProtocol {
  sealed trait Command
  sealed trait Result

  case class Put(key: String, value: JsValue, replyTo: ActorRef) extends Command
  case class PutConfirmed(key: String, value: JsValue) extends Result
  case class PutRejected(key: String, value: JsValue) extends Result
  case class Get(key: String, replyTo: ActorRef) extends Command
  case class GetResult(key: String, value: Option[JsValue]) extends Result
}

object Persistence {
  case class Database(seq: Int, kv: Map[String, JsValue])
  object Database { implicit val format = Json.format[Database] }

  def persist(name: String, seq: Int, kv: Map[String, JsValue]): Unit = {
    val bytes = Json.stringify(Json.toJson(Database(seq, kv)))
    val current = new File(s"./theDataBase-$name.json")
    val next = new File(s"./theDataBase-$name.json.new")
    IO.write(next, bytes)
    IO.move(next, current) // atomically update the database
  }
  def readPersisted(name: String): Database = {
    val file = new File(s"theDataBase-$name.json")
    if (file.exists()) Json.parse(IO.read(file)).as[Database]
    else Database(0, Map.empty)
  }
}