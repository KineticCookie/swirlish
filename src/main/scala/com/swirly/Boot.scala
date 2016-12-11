package com.swirly

/**
  * Created by Bulat on 29.11.2016.
  */

import java.io.File
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.swirly.actors.GraphActor
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.swirly.messages.{GetCurrentJob, UpdateGraph}
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Boot extends App {
  val conf = ConfigFactory.load()

  val addr = Try {
    conf.getString("mist.host")
  }
  val port = Try {
    conf.getString("mist.port")
  }
  println(s"Mist server at ${addr.get}:${port.get}")

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
        }
    } ~
      post {
        path("upload") {
          entity(as[String]) { data =>
            currentGraph ! UpdateGraph
            complete(s"$data recieved")
          }
        }
      }

  Http().bindAndHandle(routes, "localhost", 8080)
}
