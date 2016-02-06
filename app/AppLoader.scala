import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import controllers.{Application, Assets}
import filters.StatsFilter
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ning.NingWSComponents
import play.api.mvc.{Filter, EssentialFilter}
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire._
import services.{WeatherService, SunService}

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    Logger.configure(context.environment)
    (new BuiltInComponentsFromContext(context) with AppComponents).application
  }
}

trait AppComponents extends BuiltInComponents with NingWSComponents {
  lazy val assets: Assets = wire[Assets]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[Application]

  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]
  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)

  lazy val statsActor = actorSystem.actorOf(
    Props(wire[StatsActor]), StatsActor.name)


  applicationLifecycle.addStopHook { () =>
    Logger.info("The app is about to stop")
    Future.successful(Unit)
  }

  val onStart = {
    Logger.info("The app is about to start")
    statsActor ! Ping
  }
}
