package com.swirly.data

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by bulat on 06.01.17.
  */
case class HistoryData(time: Long, data: Map[String, Any])

object HistoryDataImplicits extends DefaultJsonProtocol {
  import com.swirly.utils.MapFormat._
  implicit val HistoryDataFormat: RootJsonFormat[HistoryData] = jsonFormat2(HistoryData)
}