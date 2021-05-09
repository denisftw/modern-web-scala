package controllers

import model.CombinedData
import controllers.Assets.Asset
import play.api.libs.json.Json
import play.api.mvc._
import services._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Form
import play.api.data.Forms._

case class UserLoginData(username: String, password: String)

class Application(components: ControllerComponents, assets: Assets,
    sunService: SunService, weatherService: WeatherService,
    statsService: StatsService, authService: AuthService,
    userAuthAction: UserAuthAction)
    extends AbstractController(components) {

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
    val requestsF = statsService.getRequestCount

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
    userDataForm
      .bindFromRequest()
      .fold(
        formWithErrors => Ok(views.html.login(Some("Wrong data"))),
        userData => {
          val maybeCookie =
            authService.login(userData.username, userData.password)
          maybeCookie match {
            case Some(cookie) =>
              Redirect("/").withCookies(cookie)
            case None =>
              Ok(views.html.login(Some("Login failed")))
          }
        }
      )
  }

  private val userDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginData.apply)(UserLoginData.unapply)
  }
}
