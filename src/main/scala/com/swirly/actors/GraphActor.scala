package com.swirly.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.sandinh.paho.akka.Publish
import com.swirly.{Configs, Constants}
import com.swirly.data._
import com.swirly.messages._
import com.swirly.utils.Time
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor(val mqttAck: ActorRef) extends Actor {
  import com.swirly.data.JobRequestImplicits._

  val log = Logging(context.system, this)

  def evaluate(graph :DAGraph, history: Map[UUID, ListBuffer[HistoryData]], msgQueue: Map[Link, mutable.Queue[JobResult]]): Receive = {

    case StreamData(payload) =>
      log.info("Recieved streaming job data...")

      val roots = graph.roots
      sendJobRequests(roots, payload)

    case JobFinished(uuid, result) =>
      import com.swirly.utils.MapFormat._
      import spray.json._
      import DefaultJsonProtocol._

      log.info("Recieved job result data...")
      history(uuid) += HistoryData(Time.unixNow, result.payload)
      log.info(s"Publishing results to swirlish/$uuid")
      mqttAck ! Publish(s"swirlish/$uuid", result.payload.toJson.toString.getBytes(Constants.StringEncoding), 0)

      val outLinks = graph.out(uuid)
      outLinks.foreach { link =>
        msgQueue(link) += result
        log.info(s"Added message to ${link.destination} job queue")
      }

      val nextJobs = graph.nodes.filter { node =>
        outLinks.map(_.destination).contains(node.uid)
      }
      nextJobs.foreach { job =>
        val inLinks = graph.in(job)
        var msgsReady = true
        inLinks.foreach { link =>
          val queue = msgQueue(link)
          if (queue.isEmpty) msgsReady = false
        }
        if (msgsReady) {
          val msgs = inLinks.map { link =>
            msgQueue(link).dequeue()
          }
          val payload = msgs.map(_.payload).reduce(_ ++ _)
          log.info(s"Job ${job.uid} has collected all data. Starting job...")
          sendJobRequest(job, payload)
        }
      }

    case GetHistory(id) =>
      sender() ! history(id).toList

    case UpdateGraph(newGraph) =>
      updateGraph(newGraph)

    case GetGraph =>
      sender() ! graph
  }

  def receive: Receive = {
    case UpdateGraph(graph) =>
      updateGraph(graph)

    case GetGraph =>
      sender() ! DAGraph(links = Seq.empty, nodes = Seq.empty)
  }

  def sendJobRequest(node: Node, data: Map[String, Any]) = {
    import spray.json._
    val request = JobRequest(
      route = node.url,
      parameters = data,
      externalId = Some(node.uid.toString)
    )
    mqttAck ! Publish(Configs.Mist.Mqtt.subscribeTopic, request.toJson.toString.getBytes(Constants.StringEncoding), 0)
  }

  def sendJobRequests(nodes: Seq[Node], data: Map[String, Any]) = {
    nodes.foreach(sendJobRequest(_, data))
  }

  def updateGraph(graph: DAGraph) = {
    log.debug("Graph update recieved")
    val msgQueue = graph.links.map { link =>
      link -> mutable.Queue.empty[JobResult]
    }.toMap
    val history = graph.nodes.map { node =>
      node.uid -> ListBuffer.empty[HistoryData]
    }.toMap
    log.debug("Graph updated")
    context.become(evaluate(graph, history, msgQueue))
  }
}
