import StreamBuddy.RowStreamed
import StreamReceiver.Audit
import akka.actor.Status.Failure
import akka.actor.{ Actor, ActorRef }

/**
 * Created by kolemannix (mailto:koleman@livesafemobile.com).
 */
object StreamReceiver {
  case class Audit(msg: String)
}
class StreamReceiver(auditor: ActorRef) extends Actor {

  def receive: Receive = {
    case StreamBuddy.InitMessage =>
      sender() ! StreamBuddy.AckMessage
    case StreamBuddy.CompleteMessage =>
      println("Complete!")

    case RowStreamed(domainObject: Model.DomainObject) =>
      val auditMsg = s"StreamReceiver received row with id: ${domainObject.id} at moment ${System.currentTimeMillis()}"
      println(auditMsg)
      auditor ! Audit(auditMsg)
      sender() ! StreamBuddy.AckMessage
    case Failure(ex) =>
      ex.printStackTrace
  }

}
