package model

import play.api.libs.json.Json

/**
  * Created by denis on 2/8/16.
  */
case class CombinedData(sunInfo: SunInfo, temperature: Double, requests: Int)

object CombinedData {
  implicit val writes = Json.writes[CombinedData]
}
