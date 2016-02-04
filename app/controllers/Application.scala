package controllers

import model.SunInfo
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.libs.ws.WS
import play.api.mvc._

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class Application extends Controller {

  def index = Action.async {
    val responseF = WS.url("http://api.sunrise-sunset.org/json?lat=-33.8830&lng=151.2167&formatted=0").get()
    val weatherResponseF = WS.url("http://api.openweathermap.org/data/2.5/weather?lat=-33.8830&lon=151.2167&appid=d06f9fa75ebe72262aa71dc6c1dcd118&units=metric").get()

    for {
      response <- responseF
      weatherResponse <- weatherResponseF
    } yield {
      val weatherJson = weatherResponse.json
      val temperature = (weatherJson \ "main" \ "temp").as[Double]
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = DateTime.parse(sunriseTimeStr)
      val sunsetTime = DateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(DateTimeZone.forID("Australia/Sydney"))
      val sunInfo = SunInfo(formatter.print(sunriseTime), formatter.print(sunsetTime))
      Ok(views.html.index(sunInfo, temperature))
    }
  }
}
