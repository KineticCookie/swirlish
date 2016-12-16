package com.swirly

/**
  * Created by Bulat on 29.11.2016.
  */

import java.io.File
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.swirly.actors.GraphActor
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.swirly.data.{DAGraph, Node}
import com.swirly.messages.{GetCurrentJob, StartGraph, UpdateGraph}
import spray.json._

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Boot extends App {
  val conf = ConfigFactory.load("docker.conf")

  implicit val system = ActorSystem("swirlish")
  implicit val materializer = ActorMaterializer()
  implicit val ex = system.dispatcher

  var currentGraph: ActorRef = system.actorOf(Props(classOf[GraphActor]), "graph")

  val timeoutDuration = Try {
    conf.getInt("spray.can.server.request-timeout")
  }
  implicit val timeout = timeoutDuration match {
    case Success(value) => Timeout(value.seconds)
    case Failure(_) => Timeout(5.seconds)
  }

  import scala.collection.JavaConversions._
  import DefaultJsonProtocol._

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
        path("current") {
          val status = (currentGraph ? GetCurrentJob).mapTo[UUID]
          onComplete(status) {
            case Success(data) =>
              complete {
                data.toString
              }
            case Failure(_) =>
              complete {
                "Operation failed"
              }
          }
        } ~
        path("status") {
          complete {
            ("Hello", "My man")
          }
        } ~
      path("test") {
        complete {
          import com.swirly.data.DAGraphImplicits._

          val node1 = Node(UUID.randomUUID(), "url1")
          val node2 = Node(UUID.randomUUID(), "url2")
          val node3 = Node(UUID.randomUUID(), "url3")
          val node4 = Node(UUID.randomUUID(), "url4")
          val nodes = Seq(node1, node2, node3, node4)
          val edges = Seq(
            node1 -> node2,
            node2 -> node4,
            node3 -> node4,
            node3 -> node2,
            node1 -> node3
          )
          DAGraph(nodes, edges)
        }
      } ~
      path("run") {
        complete {
          currentGraph ! StartGraph
          "Started"
        }
      }
    } ~
      post {
        path("upload") {
          import com.swirly.data.DAGraphImplicits._

          entity(as[DAGraph]) { data =>
            currentGraph ! UpdateGraph(data)
            complete(s"$data recieved")
          }
        }
      }

  Http().bindAndHandle(routes, "localhost", 8080)
}
