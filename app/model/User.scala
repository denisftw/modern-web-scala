package model

import java.util.UUID

import scalikejdbc.WrappedResultSet

case class User(userId: UUID, userCode: String, password: String)

object User {
  def fromRS(rs: WrappedResultSet): User = {
    User(UUID.fromString(rs.string("user_id")),
      rs.string("user_code"), rs.string("password"))
  }
}