package services

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

import scala.concurrent.{Future, Promise}

class StatsService {
  private val stateActor = ActorSystem(behaviour(0), "stateActor")

  def recordRequest(): Unit = {
    stateActor ! RecordRequest
  }

  def getRequestCount: Future[Int] = {
    val result = Promise[Int]()
    stateActor ! GetRequestCount(result)
    result.future
  }

  private def behaviour(counter: Int): Behavior[Stats] =
    Behaviors.receiveMessage {
      case RecordRequest =>
        behaviour(counter + 1)
      case GetRequestCount(result) =>
        result.success(counter)
        Behaviors.same
    }

  sealed trait Stats
  case object RecordRequest extends Stats
  case class GetRequestCount(result: Promise[Int]) extends Stats
}
