import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import controllers.Application
import filters.StatsFilter
import play.api.ApplicationLoader.Context
import play.api._
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import router.Routes
import play.api.routing.Router
import com.softwaremill.macwire._
import play.api.cache.caffeine.CaffeineCacheComponents
import _root_.controllers.AssetsComponents
import scalikejdbc.config.DBs
import services.{AuthService, SunService, UserAuthAction, WeatherService}

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach { configurator =>
      configurator.configure(context.environment)
    }
    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents with EvolutionsComponents with DBComponents
  with HikariCPComponents with CaffeineCacheComponents with AssetsComponents {

  private val log = Logger(this.getClass)

  override lazy val controllerComponents = wire[DefaultControllerComponents]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[Application]

  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]
  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)

  lazy val authService = new AuthService(defaultCacheApi.sync)

  lazy val userAuthAction = wire[UserAuthAction]

  override lazy val dynamicEvolutions = new DynamicEvolutions

  lazy val statsActor = actorSystem.actorOf(
    Props(wire[StatsActor]), StatsActor.name)


  applicationLifecycle.addStopHook { () =>
    log.info("The app is about to stop")
    DBs.closeAll()
    Future.successful(Unit)
  }

  val onStart = {
    log.info("The app is about to start")
    DBs.setupAll()
    applicationEvolutions
    statsActor ! Ping
  }
}
