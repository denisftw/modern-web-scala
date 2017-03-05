package services

import model.User
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


case class UserAuthRequest[A](user: User, request: Request[A]) extends  WrappedRequest[A](request)

class UserAuthAction(authService: AuthService, ec: ExecutionContext,
                     playBodyParsers: PlayBodyParsers)
  extends ActionBuilder[UserAuthRequest, AnyContent] {

  override val executionContext = ec
  override def parser = playBodyParsers.defaultBodyParser

  def invokeBlock[A](request: Request[A],
                     block: (UserAuthRequest[A]) => Future[Result]): Future[Result] = {
    val maybeUser = authService.checkCookie(request)
    maybeUser match {
      case None => Future.successful(Results.Redirect("/login"))
      case Some(user) => block(UserAuthRequest(user, request))
    }
  }
}
