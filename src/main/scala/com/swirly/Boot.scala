package com.swirly

/**
  * Created by Bulat on 29.11.2016.
  */

import java.io.File
import java.util.UUID
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern.ask
import akka.util.Timeout
import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings
import com.sandinh.paho.akka.{MqttPubSub, PSConfig}
import com.swirly.actors.{GraphActor, StreamListenerActor}
import com.swirly.data.{DAGraph, HistoryData, Node}
import com.swirly.messages.{GetGraph, GetHistory, UpdateGraph}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Boot extends App {
  import com.swirly.data.DAGraphImplicits._

  import scala.collection.JavaConversions._

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

  val currentGraph = system.actorOf(Props(classOf[GraphActor], mqttActor), Constants.Actors.Graph)

  val streamActor = system.actorOf(Props(classOf[StreamListenerActor], mqttActor, currentGraph, mqttListenTopic), Constants.Actors.StreamListener)

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
            DAGraph(nodes, edges)
          }
        } ~
        path("current") {
          val f = currentGraph ? GetGraph
          onSuccess(f) { graph =>
            complete {
              graph.asInstanceOf[DAGraph]
            }
          }
        } ~
        path("history") {
          import com.swirly.data.HistoryDataImplicits._
          parameter("id") { id =>
            val uuid = UUID.fromString(id)
            val f = currentGraph ? GetHistory(uuid)
            onSuccess(f) { aHistory =>
              complete {
                aHistory.asInstanceOf[List[HistoryData]]
              }
            }
          }
        }
    } ~
      post {
        path("upload") {
          entity(as[DAGraph]) { data =>
            currentGraph ! UpdateGraph(data)
            complete(s"$data recieved")
          }
        }
      }
  }
  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
