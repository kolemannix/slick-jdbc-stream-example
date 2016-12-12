import Model.DomainObject
import akka.NotUsed
import akka.actor.{ ActorRef, ActorSystem }
import akka.stream.{ ActorAttributes, ActorMaterializer, Supervision }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import org.reactivestreams.{ Subscriber, Subscription }


/**
 * Created by kolemannix (mailto:koleman@livesafemobile.com).
 */
object StreamBuddy {
  case class RowStreamed[A](row: A)

  case object InitMessage
  case object AckMessage
  case object CompleteMessage
}

class StreamBuddy(system: ActorSystem, receiver: ActorRef) {
  import StreamBuddy._

  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)

  def source(): Source[DomainObject, NotUsed] = {
    val pub = Repository.streamDomainObjects()
    Source.fromPublisher(pub)
  }

  val conversionFlow: Flow[DomainObject, RowStreamed[DomainObject], NotUsed] = Flow.fromFunction(obj => RowStreamed(obj))

  val sink: Sink[Any, NotUsed] = Sink.actorRefWithAck(receiver, InitMessage, AckMessage, CompleteMessage)

  val decider: Supervision.Decider = {
    case ex: Throwable => ex.printStackTrace(); Supervision.Stop
  }

  def run(): Unit =
    source().via(conversionFlow).to(sink).withAttributes(ActorAttributes.supervisionStrategy(decider)).run()

}
