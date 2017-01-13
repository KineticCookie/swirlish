package com.swirl

/**
  * Created by Bulat on 29.11.2016.
  */

import java.io.File
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.megard.akka.http.cors.CorsSettings
import com.sandinh.paho.akka.{MqttPubSub, PSConfig}
import com.swirl.Messages.GraphActor.{GetGraph, GetHistory}
import com.swirl.Messages.MqttActor.UpdateGraph
import com.swirl.actors.{GraphActor, MqttActor}
import com.swirl.data.HistoryData
import com.swirl.data.dag.{Graph, Node}
import com.typesafe.config.ConfigFactory

object Boot extends App {

  import spray.json._
  import DefaultJsonProtocol._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import akka.http.scaladsl.server.Directives._
  import ch.megard.akka.http.cors.CorsDirectives._

  import scala.collection.JavaConversions._
  import scala.concurrent.duration._

  implicit val system = ActorSystem(Constants.Actors.ActorSystem)
  implicit val materializer = ActorMaterializer()
  implicit val ex = system.dispatcher
  implicit val timeout = Timeout(10.seconds)

  val mqttAddr = Configs.Mist.Mqtt.host
  val mqttPort = Configs.Mist.Mqtt.port
  val mqttListenTopic = Configs.Mist.Mqtt.publishTopic

  val mqttActor = system.actorOf(Props(classOf[MqttPubSub], PSConfig(
    brokerUrl = s"tcp://$mqttAddr:$mqttPort",
    userName = null,
    password = null,
    stashTimeToLive = 1.minute,
    stashCapacity = 8000, //stash messages will be drop first haft elems when reach this size
    reconnectDelayMin = 10.millis, //for fine tuning re-connection logic
    reconnectDelayMax = 30.seconds
  )), Constants.Actors.Mqtt)
  val executorActor = system.actorOf(Props(classOf[MqttActor], mqttActor), Constants.Actors.StreamListener)
  val settings = CorsSettings.defaultSettings
  val routes = cors(settings) {
    get {
      path("available") {
        complete {
          val routesConf = ConfigFactory.parseFile(new File(Constants.Paths.Routes))
          val keys = routesConf.root().keys.toList
          val res = keys.map { k =>
            val job = "job" -> k
            val namespace = "namespace" -> routesConf.getString(s"$k.namespace")
            Map(job, namespace)
          }
          res
        }
      } ~
        path("test") {
          complete {
            val node1 = Node(UUID.randomUUID(), "sum")
            val node2 = Node(UUID.randomUUID(), "square")
            val node3 = Node(UUID.randomUUID(), "double")
            val nodes = Seq(node1, node2, node3)
            val edges = Seq(
              node1 -> node2,
              node2 -> node3
            )
            Graph(nodes, edges)
          }
        } ~
        path("current") {
          currentGraph match {
            case Some(graph) =>
              val future = graph ? GetGraph
              onSuccess(future) { g =>
                complete {
                  g.asInstanceOf[Graph]
                }
              }
            case None =>
              complete {
                Graph(Seq.empty, Seq.empty)
              }
          }
        } ~
        path("history") {
          parameter("id") { id =>
            val uuid = UUID.fromString(id)
            currentGraph match {
              case Some(graph) =>
                val future = graph ? GetHistory(uuid)
                onSuccess(future) { aHistory =>
                  complete {
                    aHistory.asInstanceOf[List[HistoryData]]
                  }
                }
              case None =>
                complete {
                  List.empty[List[HistoryData]]
                }
            }
          }
        }
    } ~
      post {
        path("upload") {
          entity(as[Graph]) { data =>
            val newGraphAck = system.actorOf(Props(classOf[GraphActor], data, mqttActor), Constants.Actors.Graph)
            currentGraph = Some(newGraphAck)
            executorActor ! UpdateGraph(newGraphAck)
            complete(s"$data recieved")
          }
        }
      }
  }
  var currentGraph = Option.empty[ActorRef]
  Http().bindAndHandle(routes, Configs.Swirl.Http.host, Configs.Swirl.Http.port)
}
