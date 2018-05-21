/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

package chapter11

import java.net.URL

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.testkit.TestProbe
import chapter11.DataIngesterSpec.{ DataIngester, EOF, Record, Retrieve }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

class DataIngesterSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  private implicit lazy val system: ActorSystem = ActorSystem()

  "Matching responses to requests with a correlation ID" in {
    import scala.concurrent.duration._

    val url = new URL("http://rdp.reactiveplatform.xyz")
    // #snip
    val ingestService = system.actorOf(DataIngester.props)
    val probe = TestProbe()
    ingestService ! Retrieve(url, "myID", probe.ref)
    val replies = probe.receiveWhile(1.second) {
      case r @ Record("myID", _) ⇒ r
    }
    probe.expectMsg(0.seconds, EOF)
    // #snip

  }

  override protected def afterAll(): Unit = {
    system.terminate()
  }
}

object DataIngesterSpec {
  sealed trait DataIngesterCommand
  final case class Retrieve(url: URL, dataId: String, replyTo: ActorRef) extends DataIngesterCommand

  sealed trait DataIngesterEvent
  final case class Record(dataId: String, data: Any) extends DataIngesterEvent
  final case object EOF extends DataIngesterEvent

  class DataIngester extends Actor {
    override def receive: Receive = {
      case Retrieve(_, dataId, replyTo) ⇒
        replyTo ! Record(dataId, 1)
        replyTo ! Record(dataId, 2)
        replyTo ! Record(dataId, 3)
        // finished
        replyTo ! EOF
    }
  }

  object DataIngester {
    val props: Props = Props[DataIngester]
  }
}
