package com.swirly.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.sandinh.paho.akka.{Message, Subscribe, SubscribeAck}
import com.swirly.Constants
import com.swirly.data.JobResult
import com.swirly.messages.{StreamData, _}
/**
  * Created by bulat on 21.12.16.
  */
class StreamListenerActor(val mqttSub: ActorRef, val graphActor: ActorRef, val topic: String) extends Actor {
  import com.swirly.data.JobResultImplicits._

  val logger = Logging(context.system, this)
  mqttSub ! Subscribe(topic, self)

  def receive = {
    case SubscribeAck(Subscribe(`topic`, `self`, _), fail) =>
      if (fail.isEmpty) context become ready
      else logger.error(fail.get, s"Can't subscribe to $topic")
  }

  def ready: Receive = {
    case msg: Message =>
      import com.swirly.utils.MapFormat._
      import spray.json._
      import DefaultJsonProtocol._

      val str = new String(msg.payload, Constants.StringEncoding)
      logger.info(s"Recieved $str @ ${msg.topic}")

      val json = str.parseJson
      val jsObj = json.asJsObject
      if(jsObj.fields.contains("request")) {
        val resp = json.convertTo[JobResult]
        resp.request.externalId foreach { id =>
          val uuid = UUID.fromString(id)
          graphActor ! JobFinished(uuid, resp)
        }
      } else if (!jsObj.fields.contains("route")) {
        val resp = json.convertTo[Map[String, Any]]
        graphActor ! StreamData(resp)
      }
  }

}