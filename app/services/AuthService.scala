package services

import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

import model.User
import org.apache.commons.codec.binary.Base64
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.CacheApi
import play.api.mvc.Cookie
import scalikejdbc._

import scala.concurrent.duration.Duration

/**
  * Created by denis on 2/11/16.
  */
class AuthService(cacheApi: CacheApi) {

  def login(userCode: String, password: String): Option[Cookie] = {
    for {
      user <- checkUser(userCode, password)
      cookie <- Some(createCookie(user))
    } yield {
      cookie
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
    val token = Base64.encodeBase64String(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }
}
