package services

import model.User
import play.api.mvc._

import scala.concurrent.Future


case class UserAuthRequest[A](user: User, request: Request[A]) extends  WrappedRequest[A](request)

class UserAuthAction(authService: AuthService)
    extends ActionBuilder[UserAuthRequest] {

  def invokeBlock[A](request: Request[A],
            block: (UserAuthRequest[A]) => Future[Result]): Future[Result] = {
    val maybeUser = authService.checkCookie(request)
    maybeUser match {
      case None => Future.successful(Results.Redirect("/login"))
      case Some(user) => block(UserAuthRequest(user, request))
    }
  }
}
