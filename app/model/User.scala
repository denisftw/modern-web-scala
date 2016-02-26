package model

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.{Json, Writes}
import scalikejdbc.WrappedResultSet

/**
  * Created by denis on 2/26/16.
  */
case class User(userId: UUID, userCode: String, firstName: Option[String],
                lastName: Option[String], created: DateTime)

object User {
  def fromDb(rs: WrappedResultSet): User = {
    val idStr = rs.string("user_id")
    val userCode = rs.string("user_code")
    val firstName = rs.stringOpt("first_name")
    val lastName = rs.stringOpt("last_name")
    val created = rs.jodaDateTime("created")
    User(UUID.fromString(idStr), userCode, firstName, lastName, created)
  }

  implicit val writes: Writes[User] = Json.writes[User]
}