import org.scalamock.scalatest.MockFactory

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId, ZonedDateTime}
import org.scalatestplus.play._
import org.scalatest.concurrent._
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcWSRequest
import play.api.libs.ws.{WSClient, WSResponse}
import services.SunService

import scala.concurrent.Future


/**
  * Created by denis on 4/15/16.
  */
class ApplicationSpec extends PlaySpec with MockFactory with ScalaFutures {

  "DateTimeFormat" must {
    "return 1970 as the beginning of epoch" in {
      val beginning = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0),
        ZoneId.systemDefault())
      val formattedYear = beginning.format(DateTimeFormatter.ofPattern("YYYY"))
      formattedYear mustBe "1970"
    }
  }

  "SunService" must {
    "retrieve correct sunset and sunrise information" in {
      val wsClientStub = stub[WSClient]
      val wsRequestStub = stub[AhcWSRequest]
      val wsResponseStub = stub[WSResponse]

      val expectedResponse = """
            {
              "results":{
               "sunrise":"2016-04-14T20:18:12+00:00",
               "sunset":"2016-04-15T07:31:52+00:00"
              }
            }
      """
      val jsResult = Json.parse(expectedResponse)

      val lat = -33.8830
      val lon = 151.2167
      val url = "http://api.sunrise-sunset.org/" +
        s"json?lat=$lat&lng=$lon&formatted=0"

      (() => wsResponseStub.json).when().returns(jsResult)
      (wsRequestStub.get _).when().returns(
        Future.successful(wsResponseStub))
      (wsClientStub.url _).when(url).returns(wsRequestStub)

      val sunService = new SunService(wsClientStub)
      val resultF = sunService.getSunInfo(lat, lon)

      whenReady(resultF) { res =>
        res.sunrise mustBe "06:18:12"
        res.sunset mustBe "17:31:52"
      }
    }
  }
}
