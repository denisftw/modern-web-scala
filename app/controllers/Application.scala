package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.util.Timeout
import model.CombinedData
import controllers.Assets.Asset
import play.api.libs.json.Json
import play.api.mvc._
import akka.pattern.ask
import services._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Form
import play.api.data.Forms._

case class UserLoginData(username: String, password: String)

class Application(components: ControllerComponents, assets: Assets,
    sunService: SunService,
    weatherService: WeatherService,
    actorSystem: ActorSystem,
    authService: AuthService,
    userAuthAction: UserAuthAction) extends AbstractController(components) {

  def index = Action {
    Ok(views.html.index())
  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)

  def restricted = userAuthAction { userAuthRequest =>
    Ok(views.html.restricted(userAuthRequest.user))
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
    Ok(views.html.login(None))
  }

  def doLogin = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.login(Some("Wrong data"))),
      userData => {
        val maybeCookie = authService.login(userData.username, userData.password)
        maybeCookie match {
          case Some(cookie) =>
            Redirect("/").withCookies(cookie)
          case None =>
            Ok(views.html.login(Some("Login failed")))
        }
      }
    )
  }

  val userDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginData.apply)(UserLoginData.unapply)
  }
}
