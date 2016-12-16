package com.swirly.data

import spray.json.{JsString, JsValue, JsonFormat}
/**
  * Created by bulat on 16.12.16.
  */
case class FullJobConfiguration(path: String,
                                              className: String,
                                              namespace: String,
                                              parameters: Map[String, Any] = Map(),
                                              externalId: Option[String] = None)
