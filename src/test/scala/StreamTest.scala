import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import akka.util.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ BeforeAndAfterAll, FreeSpecLike, Matchers }
import scala.concurrent.duration._

import StreamReceiver.Audit

/**
 * Created by kolemannix (mailto:koleman@livesafemobile.com).
 */
class StreamTest extends TestKit(ActorSystem("StreamTest"))
  with FreeSpecLike
  with BeforeAndAfterAll
  with ImplicitSender
  with Matchers
  with ScalaFutures {

  implicit val timeout = Timeout(3 second)

  "stream buddy should stream" in {
    val probe = TestProbe()
    val streamReceiver = system.actorOf(Props(new StreamReceiver(probe.ref)), "receiver")

    watch(streamReceiver)

    val streamBuddy = new StreamBuddy(system, streamReceiver)

    Repository.addObjects(10).futureValue

    streamBuddy.run()

    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)
    probe.expectMsgType[Audit](10 second)


  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
