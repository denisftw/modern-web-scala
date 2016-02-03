package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    import java.util.Date
    import java.text.SimpleDateFormat

    val date = new Date()
    val dateStr = new SimpleDateFormat().format(date)
    Ok(views.html.index(dateStr))
  }

}
