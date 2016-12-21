package com.swirly.data

import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}
/**
  * Created by bulat on 16.12.16.
  */
case class FullJobConfiguration(path: String,
                                className: String,
                                namespace: String,
                                parameters: Map[String, Any] = Map(),
                                externalId: Option[String] = None)

object FullJobConfigurationImplicits extends DefaultJsonProtocol {
  import com.swirly.utils.MapFormat._
  implicit val fullJobConfigurationFormat: RootJsonFormat[FullJobConfiguration] = jsonFormat5(FullJobConfiguration)
}