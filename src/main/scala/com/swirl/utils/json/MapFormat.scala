package com.swirl.utils.json

import spray.json.{DeserializationException, JsFalse, JsNumber, JsString, JsTrue, JsValue, JsonFormat}

/**
  * Created by bulat on 14.12.16.
  */
object MapFormat {
  implicit object _AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any) = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean if !b => JsFalse
      case x => throw DeserializationException(x.toString)
    }
    def read(value: JsValue) = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case x => throw DeserializationException(x.toString)
    }
  }

}