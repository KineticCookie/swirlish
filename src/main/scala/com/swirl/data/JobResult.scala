package com.swirl.data

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by bulat on 16.12.16.
  */
case class JobResult(success: Boolean,
                     payload: Map[String, Any],
                     errors: List[String],
                     request: FullJobConfiguration)

object JobResultImplicits extends DefaultJsonProtocol {
  import FullJobConfigurationImplicits._
  import com.swirl.utils.MapFormat._
  implicit val jobResultFormat: RootJsonFormat[JobResult] = jsonFormat4(JobResult)
}