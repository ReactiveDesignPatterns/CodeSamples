/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter13

// #snip_13-3
import java.io.File

import akka.actor.ActorRef
import play.api.libs.json.{ JsValue, Json, OFormat }
import sbt.io.IO

object Persistence {

  case class Database(seq: Int, kv: Map[String, JsValue])

  object Database {
    implicit val format: OFormat[Database] = Json.format[Database]
  }

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

// #snip_13-3

object ReplicationProtocol {

  // #snip_protocol
  sealed trait Command

  sealed trait Result

  case class Put(key: String, value: JsValue, replyTo: ActorRef) extends Command

  case class PutConfirmed(key: String, value: JsValue) extends Result

  case class PutRejected(key: String, value: JsValue) extends Result

  case class Get(key: String, replyTo: ActorRef) extends Command

  case class GetResult(key: String, value: Option[JsValue]) extends Result

  // #snip_protocol
}
