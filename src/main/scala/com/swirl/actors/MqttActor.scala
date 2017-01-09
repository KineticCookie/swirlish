package com.swirl.actors

import java.util.UUID

import akka.actor.ActorRef
import com.sandinh.paho.akka.{Message, Publish, Subscribe, SubscribeAck}
import com.swirl.data.JobResult
import com.swirl.messages.Messages
import com.swirl.messages.Messages.GraphActor.StartJob
import com.swirl.messages.Messages.MqttActor.UpdateGraph
import com.swirl.{Configs, Constants}

/**
  * Created by bulat on 21.12.16.
  */
class MqttActor(val mqttAck: ActorRef) extends LoggableActor {
  import com.swirl.data.JobRequestImplicits._
  import com.swirl.data.JobResultImplicits._
  import spray.json._

  private val listenTopic = Configs.Mist.Mqtt.publishTopic
  private val publishTopic = Configs.Mist.Mqtt.subscribeTopic
  private val jobsTopic = Configs.Swirl.jobsTopic

  mqttAck ! Subscribe(listenTopic, self)

  def receive: Receive = {
    case UpdateGraph(graphActor) =>
      log.warning("Recieved graph without MQTT")
      context become graphDisconnected(graphActor)
    case SubscribeAck(Subscribe(`listenTopic`, `self`, _), fail) =>
      if (fail.isEmpty) {
        log.info(s"Subsctibed to $listenTopic")
        context become graphless
      }
      else log.error(fail.get, s"Can't subscribe to $listenTopic")
  }

  def graphDisconnected(graphActor: ActorRef): Receive = {
    case SubscribeAck(Subscribe(`listenTopic`, `self`, _), fail) =>
      if (fail.isEmpty) {
        log.info(s"Subsctibed to $listenTopic")
        context become ready(graphActor)
      }
      else log.error(fail.get, s"Can't subscribe to $listenTopic")
  }

  def graphless(): Receive = {
    case UpdateGraph(graphActor) =>
      log.info("Recieved graph actor...")
      context become ready(graphActor)
  }

  def ready(graphActor: ActorRef): Receive = {
    case UpdateGraph(newActor) =>
      log.info("Graph update msg recieved")
      context become ready(newActor)

    case StartJob(jobRequest) =>
      log.info(s"Starting ${jobRequest.route} with Id:${jobRequest.externalId} job")
      mqttAck ! Publish(publishTopic, jobRequest.toJson.toString.getBytes(Constants.StringEncoding), 0)

    case msg: Message =>
      import com.swirl.utils.MapFormat._
      import spray.json._
      import DefaultJsonProtocol._

      val str = new String(msg.payload, Constants.StringEncoding)

      val json = str.parseJson
      val jsObj = json.asJsObject
      if(jsObj.fields.contains("request")) {
        val resp = json.convertTo[JobResult]
        resp.request.externalId foreach { id =>
          val uuid = UUID.fromString(id)
          graphActor ! Messages.MqttActor.JobFinished(uuid, resp)

          log.info(s"Publishing results to $jobsTopic/$uuid")
          mqttAck ! Publish(s"$jobsTopic/$uuid", resp.payload.toJson.toString.getBytes(Constants.StringEncoding), 0)
        }
      } else if (!jsObj.fields.contains("route")) {
        val resp = json.convertTo[Map[String, Any]]
        graphActor ! Messages.MqttActor.StreamData(resp)
      }
  }
}
