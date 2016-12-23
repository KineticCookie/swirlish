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
import com.sandinh.paho.akka.{MqttPubSub, PSConfig}
import com.swirly.actors.{GraphActor, StreamListenerActor}
import com.swirly.data.{DAGraph, Node}
import com.swirly.messages.UpdateGraph
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

object Boot extends App {
  import scala.collection.JavaConversions._
  import com.swirly.data.DAGraphImplicits._

  val conf = ConfigFactory.load(Constants.Paths.Docker)

  implicit val system = ActorSystem(Constants.Actors.ActorSystem)
  implicit val materializer = ActorMaterializer()
  implicit val ex = system.dispatcher

  val mqttAddr = "172.17.0.2"//conf.getString("mist.mqtt.host")
  val mqttPort = conf.getString(Constants.Config.Mist.Mqtt.Port)

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

  val streamActor = system.actorOf(Props(classOf[StreamListenerActor], mqttActor, currentGraph, "swirlish_pub"), Constants.Actors.StreamListener)

  val routes =
    get {
      path("available") {
        complete {
          val sysProps = System.getProperties
          val routesConf = ConfigFactory.parseFile(new File("src/main/resources/routes.conf"))
          val root = routesConf.root()
          val routes = root.filter { x =>
            !sysProps.containsKey(x._1)
          }
          routes.keys.toList
        }
      } ~
        path("available2") {
          complete {
            val routesConf = ConfigFactory.parseFile(new File("src/main/resources/routes.conf"))
            val keys = routesConf.root().keys.toList
            val res = keys.map{ k =>
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

  Http().bindAndHandle(routes, "localhost", 8080)
}
