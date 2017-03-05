package services

import play.api.libs.ws.WSClient

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by denis on 2/5/16.
  */
class WeatherService(wsClient: WSClient) {
  def getTemperature(lat: Double, lon: Double): Future[Double] = {
    val weatherResponseF = wsClient.url("http://api.openweathermap.org/data/2.5/" +
      s"weather?lat=$lat&lon=$lon&appid=d06f9fa75ebe72262aa71dc6c1dcd118&units=metric").get()
    weatherResponseF.map { weatherResponse =>
      val weatherJson = weatherResponse.json
      val temperature = (weatherJson \ "main" \ "temp").as[Double]
      temperature
    }
  }
}
