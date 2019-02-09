package services

import java.security.MessageDigest
import java.util.{Base64, UUID}
import java.util.concurrent.TimeUnit

import model.User
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import play.api.mvc.{Cookie, RequestHeader}
import scalikejdbc._

import scala.concurrent.duration.Duration

/**
  * Created by denis on 2/11/16.
  */
class AuthService(cacheApi: SyncCacheApi) {

  def login(userCode: String, password: String): Option[Cookie] = {
    for {
      user <- checkUser(userCode, password)
      cookie <- Some(createCookie(user))
    } yield {
      cookie
    }
  }

  def checkCookie(header: RequestHeader): Option[User] = {
    for {
      cookie <- header.cookies.get(cookieHeader)
      user <- cacheApi.get[User](cookie.value)
    } yield {
      user
    }
  }

  private def checkUser(userCode: String, password: String): Option[User] =
    DB.readOnly { implicit session =>
    val maybeUser = sql"select * from users where user_code = $userCode".
      map(User.fromRS).single().apply()
    maybeUser.flatMap { user =>
      if (BCrypt.checkpw(password, user.password)) {
        Some(user)
      } else None
    }
  }

  val mda = MessageDigest.getInstance("SHA-512")
  val cookieHeader = "X-Auth-Token"

  private def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.userId.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }
}
