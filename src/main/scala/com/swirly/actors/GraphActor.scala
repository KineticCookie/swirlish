package com.swirly.actors

import java.util.UUID
import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.sandinh.paho.akka.Publish
import com.swirly.Constants
import com.swirly.data.{DAGraph, JobRequest, JobResult}
import com.swirly.messages._
import com.typesafe.config.ConfigFactory

/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor(val mqttAck: ActorRef) extends Actor {
  import com.swirly.data.JobRequestImplicits._
  import com.swirly.data.JobResultImplicits._

  val log = Logging(context.system, this)

  val conf = ConfigFactory.load(Constants.Paths.Docker)

  private var currentGraph :Option[DAGraph] = None

  override def receive: Receive = {
    case StreamData(payload) =>
      import com.swirly.utils.MapFormat._
      import spray.json._
      import DefaultJsonProtocol._

      val json = payload.parseJson
      val jsObj = json.asJsObject
      if(jsObj.fields.contains("request")) {
        log.info("Recieved job result...")
        val resp = json.convertTo[JobResult]
        val id = resp.request.externalId.get
        val uuid = UUID.fromString(id)

        if(currentGraph.isEmpty) {
          log.warning("No graph defined")
        }
        currentGraph.foreach { graph =>
          val jobIdx = graph.out(uuid).map(l => l.destination)
          val nextJobs = graph.nodes.filter(n => jobIdx.contains(n.uid))
          nextJobs.foreach { n =>
            val request = JobRequest(
              route = n.url,
              parameters = resp.payload,
              externalId = Some(n.uid.toString)
            )
            mqttAck ! Publish("swirlish_pub",request.toJson.toString.getBytes("utf-8"), 0)
            log.info(s"SEND RESULT swirlish/$id")
            mqttAck ! Publish(s"swirlish/$id", resp.payload.toJson.toString.getBytes("utf-8"), 0)
          }
        }
      } else {
        log.info("Recieved streaming job data...")

        val resp = json.convertTo[Map[String, Any]]
        currentGraph.foreach { graph =>
          val roots = graph.roots
          roots.foreach { n =>
            val request = JobRequest(
              route = n.url,
              parameters = resp,
              externalId = Some(n.uid.toString)
            )
            mqttAck ! Publish("swirlish_pub",request.toJson.toString().getBytes("utf-8"), 0)
          }
        }
      }

    case UpdateGraph(graph) =>
      log.debug("Graph update recieved")
      currentGraph = Some(graph)
      log.debug("Graph updated")

    case x => log.warning(s"Unknown message: $x")
  }
}
