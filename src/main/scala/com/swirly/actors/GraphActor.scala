package com.swirly.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.sandinh.paho.akka.Publish
import com.swirly.Constants
import com.swirly.data.{DAGraph, JobRequest}
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
      import spray.json._

      log.info("Recieved streaming job data...")

      val roots = graph.roots
      roots.foreach { n =>
        val request = JobRequest(
          route = n.url,
          parameters = payload,
          externalId = Some(n.uid.toString)
        )
        mqttAck ! Publish("swirlish_pub", request.toJson.toString().getBytes("utf-8"), 0)
      }


    case JobFinished(uuid, result) =>
      log.info("Recieved job result data...")

      import com.swirly.utils.MapFormat._
      import spray.json._
      import DefaultJsonProtocol._

      val jobIdx = graph.out(uuid).map(l => l.destination)
      val nextJobs = graph.nodes.filter(n => jobIdx.contains(n.uid))
      nextJobs.foreach { n =>
        val request = JobRequest(
          route = n.url,
          parameters = result.payload,
          externalId = Some(n.uid.toString)
        )
        mqttAck ! Publish("swirlish_pub", request.toJson.toString.getBytes("utf-8"), 0)

        log.info(s"SEND RESULT swirlish/$uuid")

        mqttAck ! Publish(s"swirlish/$uuid", result.payload.toJson.toString.getBytes("utf-8"), 0)
      }

  }

  def receive: Receive = {
    case UpdateGraph(graph) =>
      log.debug("Graph update recieved")
      //currentGraph = Some(graph)
      log.debug("Graph updated")
      context.become(evaluate(graph))
  }
}
