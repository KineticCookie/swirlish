package com.swirl.data

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by bulat on 06.01.17.
  */
case class HistoryData(time: Long, data: Map[String, Any])

object HistoryData extends DefaultJsonProtocol {

  import com.swirl.utils.json.MapFormat._

  implicit val HistoryDataFormat: RootJsonFormat[HistoryData] = jsonFormat2(HistoryData.apply)
}