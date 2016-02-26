package modules

import scaldi.Module
import play.api.Logger
import controllers._


class MainModule extends Module {
  binding to new Application
  binding toNonLazy new Initializer initWith(_.init()) destroyWith(_.stop())
  bind[GreetingService] to new GreetingService
}

class GreetingService {
  def greet(name: String): String = s"Hello $name"
}

class Initializer {
  def init() = {
    Logger.info("Starting...")
  }
  def stop() = {
    Logger.info("Stopping...")
  }
}
