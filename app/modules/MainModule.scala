package modules

import scaldi.Module
import controllers._
import scalikejdbc.config.DBs
import services.{GreetingService, UserService}


class MainModule extends Module {
  binding to new Application
  binding toNonLazy new Initializer initWith(_.init()) destroyWith(_.stop())
  bind[GreetingService] to new GreetingService
  binding to new UserService
}

class Initializer {
  def init() = {
    DBs.setupAll()
  }
  def stop() = {
    DBs.closeAll()
  }
}

