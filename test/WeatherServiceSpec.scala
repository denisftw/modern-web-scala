

import com.softwaremill.macwire._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.ApplicationLoader.Context
import play.api.{BuiltInComponentsFromContext, NoHttpFiltersComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import services.WeatherService

class TestAppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with NoHttpFiltersComponents
    with AhcWSComponents {

  lazy val router: Router = Router.empty
  lazy val weatherService = wire[WeatherService]
}


import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
class WeatherServiceSpec extends PlaySpec
  with OneAppPerSuiteWithComponents
  with ScalaFutures {

  override def components = new TestAppComponents(context)

  override implicit val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds))

  "WeatherService" must {
    "return a meaningful temperature" in {
      val lat = -33.8830
      val lon = 151.2167
      val resultF = components.weatherService.getTemperature(lat, lon)
      whenReady(resultF) { result =>
        result mustBe >=(-20.0)
        result mustBe <=(60.0)
      }
    }
  }
}
