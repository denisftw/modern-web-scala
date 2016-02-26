package controllers

import play.api._
import play.api.mvc._
import scaldi.{Injectable, Injector}
import play.api.libs.json.Json
import scalikejdbc._
import services.{GreetingService, UserService}

class Application(implicit inj: Injector) extends Controller with Injectable {

  val greetingService = inject[GreetingService]
  val userService = inject[UserService]

  def greet(name: String) = Action {
    val greeting = greetingService.greet(name)
    Ok(Json.toJson(greeting))
  }

  def getAllUsers = Action {
    val users = userService.allUsers
    Ok(Json.toJson(users))
  }

  def index = Action {
    Ok(views.html.index())
  }

}
