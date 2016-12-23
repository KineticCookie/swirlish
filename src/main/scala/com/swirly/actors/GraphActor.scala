package com.swirly.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.sandinh.paho.akka.Publish
import com.swirly.Constants
import com.swirly.data.{DAGraph, JobRequest, Node}
import com.swirly.messages._
import com.typesafe.config.ConfigFactory

/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor(val mqttAck: ActorRef) extends Actor {
  import com.swirly.data.JobRequestImplicits._

  val log = Logging(context.system, this)

  val conf = ConfigFactory.load(Constants.Paths.Docker)

  def evaluate(graph :DAGraph): Receive = {

    case StreamData(payload) =>
      log.info("Recieved streaming job data...")

      val roots = graph.roots
      sendJobRequest(roots, payload)

    case JobFinished(uuid, result) =>
      log.info("Recieved job result data...")

      import com.swirly.utils.MapFormat._
      import spray.json._
      import DefaultJsonProtocol._

      val jobIdx = graph.out(uuid).map(l => l.destination)
      val nextJobs = graph.nodes.filter(n => jobIdx.contains(n.uid))

      sendJobRequest(nextJobs, result.payload)

      log.info(s"Publishing results to swirlish/$uuid")

      mqttAck ! Publish(s"swirlish/$uuid", result.payload.toJson.toString.getBytes(Constants.StringEncoding), 0)

  }

  def receive: Receive = {
    case UpdateGraph(graph) =>
      log.debug("Graph update recieved")
      log.debug("Graph updated")
      context.become(evaluate(graph))
  }

  def sendJobRequest(nodes: Seq[Node], data: Map[String, Any]) = {
    import spray.json._
    nodes.foreach { n =>
      val request = JobRequest(
        route = n.url,
        parameters = data,
        externalId = Some(n.uid.toString)
      )
      mqttAck ! Publish(conf.getString(Constants.Config.Mist.Mqtt.SubscribeTopic), request.toJson.toString.getBytes(Constants.StringEncoding), 0)

    }
  }
}
