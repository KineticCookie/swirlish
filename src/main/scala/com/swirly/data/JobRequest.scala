package com.swirly.data

import com.swirly.data.FullJobConfigurationImplicits.jsonFormat5
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by bulat on 21.12.16.
  */
case class JobRequest(route: String,
                 parameters: Map[String, Any] = Map(),
                 externalId: Option[String] = None)

object JobRequestImplicits extends DefaultJsonProtocol {
  import com.swirly.utils.MapFormat._
  implicit val fullJobConfigurationFormat: RootJsonFormat[JobRequest] = jsonFormat3(JobRequest)
}
