package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.util.Timeout
import model.{CombinedData, SunInfo}
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc._
import akka.pattern.ask

import play.api.Play.current
import services.{WeatherService, SunService}
import scala.concurrent.ExecutionContext.Implicits.global

class Application(sunService: SunService,
    weatherService: WeatherService,
    actorSystem: ActorSystem) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def data = Action.async {
    val lat = -33.8830
    val lon = 151.2167
    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestsF = (actorSystem.actorSelection(StatsActor.path) ?
      StatsActor.GetStats).mapTo[Int]

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
      requests <- requestsF
    } yield {
      Ok(Json.toJson(CombinedData(sunInfo, temperature, requests)))
    }
  }

  def login = Action {
    Ok(views.html.login())
  }
}
