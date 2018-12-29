/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package ckite

import java.nio.ByteBuffer

import ckite.http.HttpServer
import ckite.mapdb.MapDBStorage
import ckite.rpc.{ FinagleThriftRpc, ReadCommand, WriteCommand }
import ckite.statemachine.StateMachine
import ckite.util.Serializer

import scala.collection.mutable
import scala.concurrent.Future

final case class Get(key: String) extends ReadCommand[Option[String]]

final case class Put(key: String, value: String) extends WriteCommand[String]

// #snip_13-8
class KVStore extends StateMachine {
  private var map = mutable.Map[String, String]()
  private var lastIndex: Long = 0

  def applyWrite: PartialFunction[(Long, WriteCommand[_]), String] = {
    case (index, Put(key: String, value: String)) ⇒
      map.put(key, value)
      lastIndex = index
      value
  }

  def applyRead: PartialFunction[ReadCommand[_], Option[String]] = {
    case Get(key) ⇒ map.get(key)
  }

  def getLastAppliedIndex: Long = lastIndex

  def restoreSnapshot(byteBuffer: ByteBuffer): Unit = {
    map = Serializer.deserialize[mutable.Map[String, String]](byteBuffer.array())
  }

  def takeSnapshot(): ByteBuffer =
    ByteBuffer.wrap(Serializer.serialize(map))

}

// #snip_13-8

// #snip_13-9
object KVStoreBootstrap extends App {
  val ckite =
    CKiteBuilder()
      .stateMachine(new KVStore())
      .rpc(FinagleThriftRpc)
      .storage(MapDBStorage())
      .build
  ckite.start()

  HttpServer(ckite).start()
}

// #snip_13-9

object SnipCkiteAPI {

  val ckite: CKiteClient = ???
  val key = "key"
  val value = "value"

  // #snip_ckite_api
  val consistentRead: Future[Option[String]] = ckite.read(Get(key))
  val possibleStaleRead: Future[Option[String]] = ckite.readLocal(Get(key))
  val write: Future[String] = ckite.write(Put(key, value))
  // #snip_ckite_api

}
