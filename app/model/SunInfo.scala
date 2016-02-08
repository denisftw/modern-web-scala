package model

import play.api.libs.json.Json

/**
  * Created by denis on 2/4/16.
  */
case class SunInfo(sunrise: String, sunset: String)

object SunInfo {
  implicit val writes = Json.writes[SunInfo]
}