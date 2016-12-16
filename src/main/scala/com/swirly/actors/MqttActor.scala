package com.swirly.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

/**
  * Created by Bulat on 16.12.2016.
  */
class MqttActor(val connectionString: String, val topic: String) extends Actor {
  val log = Logging(context.system, this)

  lazy val connectionOptions = {
    val opt = new MqttConnectOptions
    opt.setCleanSession(true)
    opt
  }

  private[this] val client = {
    val persistence = new MemoryPersistence
    val client = new MqttAsyncClient(connectionString, MqttAsyncClient.generateClientId(), persistence)
    client.subscribe(topic, 1)
    client.setCallback(new MqttActor.Callback(self))
    client
  }

  client.connect

  override def receive: Receive = {
    case msg: MqttActor.Message =>
      log.debug(s"MQTT msg received")
      context.system.eventStream.publish(msg)
    case x => log.debug(s"MqttSubscribeActor: $x received")
  }
}

private object MqttActor {
  case class Message(payload: Array[Byte]) {
    override def toString: String = new String(payload, "utf-8")
  }

  class Callback(owner: ActorRef) extends MqttCallback {

    def connectionLost(cause: Throwable): Unit = {

    }

    def deliveryComplete(token: IMqttDeliveryToken): Unit = {

    }

    def messageArrived(topic: String, message: MqttMessage): Unit = {
      owner ! Message(message.getPayload)
    }
  }
}