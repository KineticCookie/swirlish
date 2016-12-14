package com.swirly.utils

import java.util.UUID

import spray.json.{JsString, JsValue, JsonFormat}

/**
  * Created by bulat on 14.12.16.
  */
object UuidJsonFormat {
  implicit object _UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID) = JsString(x toString())

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => throw new IllegalArgumentException(x.toString) // FIXME exception
    }
  }

}