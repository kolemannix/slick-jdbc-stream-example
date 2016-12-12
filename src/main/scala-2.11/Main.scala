import scala.concurrent.Await

import StreamReceiver.Audit
import akka.actor.{ Actor, ActorSystem, Props }
import scala.concurrent.duration._

/**
 * Created by kolemannix (mailto:koleman@livesafemobile.com).
 */
object Main extends App {
  val system: ActorSystem = ActorSystem("MySqlStreamDemo")

  val auditor = system.actorOf(Props(new Actor {
    def receive = { case Audit(msg) => println(s"Auditor received: ${msg}")}
  }))

  val streamReceiver = system.actorOf(Props(new StreamReceiver(auditor)), "receiver")
  val streamBuddy = new StreamBuddy(system, streamReceiver)

  Await.result(Repository.addObjects(100), 3 seconds)

  streamBuddy.run()

  Thread.sleep(1000)

  import scala.concurrent.ExecutionContext.Implicits.global
  system.terminate() map { _ => System.exit(0) }

}
