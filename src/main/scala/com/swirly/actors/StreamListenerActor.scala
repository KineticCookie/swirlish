package com.swirly.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.sandinh.paho.akka.{Message, Subscribe, SubscribeAck}
import com.swirly.messages.StreamData

/**
  * Created by bulat on 21.12.16.
  */
class StreamListenerActor(val mqttSub: ActorRef, val graphActor: ActorRef, val topic: String) extends Actor {
  val logger = Logging(context.system, this)
  mqttSub ! Subscribe(topic, self)

  def receive = {
    case SubscribeAck(Subscribe(`topic`, `self`, _), fail) =>
      if (fail.isEmpty) context become ready
      else logger.error(fail.get, s"Can't subscribe to $topic")
  }

  def ready: Receive = {
    case msg: Message =>
      val str = new String(msg.payload, "utf-8")
      logger.info(s"Recieved $str @ ${msg.topic}")

      graphActor ! StreamData(str)
  }

}