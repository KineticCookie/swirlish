package com.swirly.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.http.scaladsl.marshalling.Marshal
import com.swirly.data.{DAGraph, JobResult}
import com.typesafe.config.ConfigFactory
import spray.json.{DeserializationException, pimpString}
import com.swirly.messages._
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttMessage}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import spray.json._
import scala.collection.mutable

/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor extends Actor {
  import com.swirly.utils.MapFormat._
  import com.swirly.data.JobResultImplicits._
  val log = Logging(context.system, this)
  override def preStart = context.system.eventStream.subscribe(self, classOf[MqttActor])
  val conf = ConfigFactory.load()
  val mqttAddr = conf.getString("mist.mqtt.host")
  val mqttPort = conf.getString("mist.mqtt.port")
  val httpAddr = conf.getString("mist.http.host")
  val httpPort = conf.getString("mist.http.port")

  var currentGraph :Option[DAGraph] = None
  val jobs = mutable.HashMap.empty[UUID, ActorRef]
  log.debug(s"GraphActor will send jobs to $httpAddr:$httpPort")

  override def receive: Receive = {
    case msg: MqttActor.Message =>
      log.debug(s"Received msg from mqtt actor: $msg")
      val json = try {
        val data = msg.toString.parseJson.convertTo[JobResult]

        currentGraph.foreach{ graph =>
          val root = graph.root
          val nextJobs = graph.out(root).map(l => jobs(l.destination))
          nextJobs.foreach(_ ! StartJob(data.payload))
        }
      } catch {
        case ex: DeserializationException =>
          log.error(ex, s"Bad JSON: $msg")
      }

    case GetCurrentJob =>
      val id = UUID.randomUUID
      log.info(s"GetCurrentJob returns $id")
      sender ! id

    case JobFinished(id, result) =>
      currentGraph.foreach{ graph =>
        val nextJobs = graph.out(id).map(l => jobs(l.destination))

        val persistence = new MemoryPersistence
        try {
          val client = new MqttClient(s"tcp://$mqttAddr:$mqttPort", MqttClient.generateClientId, persistence)
          client.connect()
          val msgTopic = client.getTopic(s"swirlish#$id")
          val mqMessage = new MqttMessage(result.toJson.toString.getBytes("utf-8"))
          msgTopic.publish(mqMessage)
          client.disconnect()
        }

        nextJobs.foreach(_ ! StartJob(result))
      }

    case UpdateGraph(graph) =>
      log.debug("Graph update recieved")
      jobs.clear
      currentGraph = Some(graph)
      graph.nodes.foreach{ node =>
        val jobActor = context.actorOf(Props(classOf[JobActor], node.uid, s"$httpAddr:$httpPort/api/${node.url}"), node.uid.toString)
        jobs += node.uid -> jobActor
      }

    case x => log.warning(s"Unknown message: $x")
  }
}
