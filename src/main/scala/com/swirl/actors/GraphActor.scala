package com.swirl.actors

import java.util.UUID

import akka.actor.ActorRef
import com.swirl.Messages.GraphActor.{GetGraph, GetHistory, StartJob}
import com.swirl.Messages.MqttActor.{JobFinished, StreamData}
import com.swirl.data._
import com.swirl.data.dag.{Graph, Link, Node}
import com.swirl.utils.Time

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor(val graph: Graph, val mqttAck: ActorRef) extends LoggableActor {
  val msgQueue: Map[Link, mutable.Queue[JobResult]] = graph.links.map { link =>
    link -> mutable.Queue.empty[JobResult]
  }.toMap
  val history: Map[UUID, ListBuffer[HistoryData]] = graph.nodes.map { node =>
    node.uid -> ListBuffer.empty[HistoryData]
  }.toMap

  def receive: Receive = {
    case StreamData(payload) =>
      log.info("Recieved streaming job data...")

      val roots = graph.roots
      sendJobRequest(roots, payload)

    case JobFinished(uuid, result) =>
      log.info("Recieved job result data...")
      history(uuid) += HistoryData(Time.unixNow, result.payload)

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

    case GetGraph =>
      sender() ! graph
  }

  def sendJobRequest(nodes: Seq[Node], data: Map[String, Any]): Unit = {
    nodes.foreach(sendJobRequest(_, data))
  }

  def sendJobRequest(node: Node, data: Map[String, Any]): Unit = {
    val request = JobRequest(
      route = node.url,
      parameters = data,
      externalId = Some(node.uid.toString)
    )
    mqttAck ! StartJob(request)
  }
}
