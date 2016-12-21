package com.swirly.data

import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}
/**
  * Created by bulat on 16.12.16.
  */
case class JobResult(success: Boolean,
                     payload: Map[String, Any],
                     errors: List[String],
                     request: FullJobConfiguration)

object JobResultImplicits extends DefaultJsonProtocol {
  import com.swirly.utils.MapFormat._
  import FullJobConfigurationImplicits._
  implicit val jobResultFormat: RootJsonFormat[JobResult] = jsonFormat4(JobResult)
}