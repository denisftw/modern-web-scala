package controllers

import model.SunInfo
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.libs.ws.WS
import play.api.mvc._

import play.api.Play.current
import services.{WeatherService, SunService}
import scala.concurrent.ExecutionContext.Implicits.global

class Application extends Controller {

  val sunService = new SunService
  val weatherService = new WeatherService

  def index = Action.async {
    val lat = -33.8830
    val lon = 151.2167
    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)
    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
    } yield {
      Ok(views.html.index(sunInfo, temperature))
    }
  }
}
