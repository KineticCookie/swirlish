package com.swirly.data

import spray.json.{JsString, JsValue, JsonFormat}
/**
  * Created by bulat on 16.12.16.
  */
case class JobResult(success: Boolean,
                     payload: Map[String, Any],
                     errors: List[String],
                     request: FullJobConfiguration)
