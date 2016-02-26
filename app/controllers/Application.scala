package controllers

import play.api._
import play.api.mvc._
import scaldi.{Injectable, Injector}
import modules.GreetingService
import play.api.libs.json.Json

class Application(implicit inj: Injector) extends Controller with Injectable {

  val greetingService = inject[GreetingService]

  def greet(name: String) = Action {
    val greeting = greetingService.greet(name)
    Ok(Json.toJson(greeting))
  }

  def index = Action {
    Ok(views.html.index())
  }

}
