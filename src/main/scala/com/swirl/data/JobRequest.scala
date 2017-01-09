package com.swirl.data

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by bulat on 21.12.16.
  */
case class JobRequest(route: String,
                 parameters: Map[String, Any] = Map(),
                 externalId: Option[String] = None)

object JobRequest extends DefaultJsonProtocol {

  import com.swirl.utils.json.MapFormat._

  implicit val fullJobConfigurationFormat: RootJsonFormat[JobRequest] = jsonFormat3(JobRequest.apply)
}
