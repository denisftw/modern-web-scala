package services

import model.User
import scalikejdbc._

/**
  * Created by denis on 2/26/16.
  */
class UserService {
  def allUsers: Seq[User] = DB.readOnly { implicit session =>
    sql"select * from users".map(User.fromDb).list().apply()
  }

  def findUser(userCode: String): Option[User] = DB.readOnly { implicit session =>
    sql"select * from users where user_code = $userCode limit 1".map(User.fromDb).headOption().apply()
  }
}