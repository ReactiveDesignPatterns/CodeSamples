/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter17

import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.cluster._
import akka.cluster.sharding._
import java.net.URI
import java.util.UUID

object ShardSupport {
  /*
   * use the shoppingCart reference as the sharding key; the partial function
   * must return both the key and the message to be forwarded, and if it does
   * not match then the message is dropped
   */
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case mc @ ManagerCommand(cmd, _, _) => cmd.shoppingCart.id.toString -> mc
    case mc @ ManagerQuery(query, _, _) => query.shoppingCart.id.toString -> mc
  }

  /*
   * allocate shoppingCarts into 256 shards based on the low 8 bits of their
   * ID’s hash; this is a total function that must be defined for all messages
   * that are forwarded
   */
  val extractShardId: ShardRegion.ExtractShardId = {
    case ManagerCommand(cmd, _, _) => toHex(cmd.shoppingCart.id.hashCode & 255)
    case ManagerQuery(query, _, _) => toHex(query.shoppingCart.id.hashCode & 255)
  }
  private def toHex(b: Int) = new java.lang.StringBuilder(2).append(hexDigits(b >> 4)).append(hexDigits(b & 15)).toString
  private val hexDigits = "0123456789ABCDEF"

  val RegionName = "ShoppingCart"
}

object ShardingExample extends App {
  val clusterConfig = ConfigFactory.parseString("""
akka.loglevel = INFO
akka.actor.provider = "akka.cluster.ClusterActorRefProvider"
akka.actor.warn-about-java-serializer-usage = off
akka.cluster.min-nr-of-members = 2
akka.remote.netty.tcp {
  hostname = localhost
  port = 0
}
akka.cluster.sharding.state-store-mode = ddata
""")
  val node1Config = ConfigFactory.parseString("akka.remote.netty.tcp.port = 2552")

  val sys1 = ActorSystem("ShardingExample", node1Config.withFallback(clusterConfig))
  val seed = Cluster(sys1).selfAddress

  def startNode(sys: ActorSystem): Unit = {
    Cluster(sys).join(seed)
    ClusterSharding(sys).start(
      typeName = ShardSupport.RegionName,
      entityProps = Props(new Manager),
      settings = ClusterShardingSettings(sys1),
      extractEntityId = ShardSupport.extractEntityId,
      extractShardId = ShardSupport.extractShardId)
  }

  startNode(sys1)

  val sys2 = ActorSystem("ShardingExample", clusterConfig)
  startNode(sys2)

  /*
   * From this point onward we can talk to the sharded shopping carts via
   * the shard region which acts as a local mediator that will send the
   * commands to the right node.
   */
  val manager = ClusterSharding(sys1).shardRegion(ShardSupport.RegionName)

  def mkURI(): URI = URI.create(UUID.randomUUID().toString)

  val customer = CustomerRef(mkURI())
  val item1, item2 = ItemRef(mkURI())
  val shoppingCart1, shoppingCart2 = ShoppingCartRef(mkURI())

  Cluster(sys1).registerOnMemberUp(
    sys1.actorOf(Props(new Actor with ActorLogging {
      manager ! ManagerCommand(SetOwner(shoppingCart1, customer), 0, self)
      manager ! ManagerCommand(AddItem(shoppingCart1, item1, 5), 1, self)
      manager ! ManagerCommand(AddItem(shoppingCart1, item1, -3), 2, self)
      manager ! ManagerCommand(AddItem(shoppingCart1, item2, 6), 3, self)
      manager ! ManagerCommand(RemoveItem(shoppingCart1, item1, 3), 4, self)
      manager ! ManagerQuery(GetItems(shoppingCart1), 5, self)

      def receive = {
        case ManagerEvent(id, event)   => log.info("success ({}): {}", id, event)
        case ManagerRejection(id, msg) => log.warning("rejected ({}): {}", id, msg)
        case ManagerResult(id, result) =>
          log.info("result ({}): {}", id, result)

          manager ! ManagerCommand(SetOwner(shoppingCart2, customer), 10, self)
          manager ! ManagerCommand(AddItem(shoppingCart2, item2, 15), 11, self)
          manager ! ManagerCommand(AddItem(shoppingCart2, item2, -3), 12, self)
          manager ! ManagerCommand(AddItem(shoppingCart2, item1, 60), 13, self)
          manager ! ManagerCommand(RemoveItem(shoppingCart2, item2, 3), 14, self)
          manager ! ManagerQuery(GetItems(shoppingCart2), 15, self)

          context.become(second)
      }
      def second: Receive = {
        case ManagerEvent(id, event)   => log.info("success ({}): {}", id, event)
        case ManagerRejection(id, msg) => log.warning("rejected ({}): {}", id, msg)
        case ManagerResult(id, result) =>
          log.info("result ({}): {}", id, result)
          sys1.terminate()
          sys2.terminate()
      }
    }), "client"))
}
