package actors

import actors.StatsActor.{GetStats, RequestReceived, Ping}
import akka.actor.Actor

/**
  * Created by denis on 2/6/16.
  */
class StatsActor extends Actor {
  private var counter = 0

  override def receive: Receive = {
    case Ping => ()
    case RequestReceived => counter += 1
    case GetStats => sender() ! counter
  }
}

object StatsActor {
  val name = "statsActor"
  val path = s"/user/$name"

  case object Ping
  case object RequestReceived
  case object GetStats
}