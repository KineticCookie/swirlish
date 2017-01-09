package com.swirl.data

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
/**
  * Created by bulat on 16.12.16.
  */
case class FullJobConfiguration(path: String,
                                className: String,
                                namespace: String,
                                parameters: Map[String, Any] = Map(),
                                externalId: Option[String] = None)

object FullJobConfiguration extends DefaultJsonProtocol {
  import com.swirl.utils.json.MapFormat._
  implicit val fullJobConfigurationFormat: RootJsonFormat[FullJobConfiguration] = jsonFormat5(FullJobConfiguration.apply)
}