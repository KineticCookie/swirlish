package com.swirly.actors

import java.io.File

import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing._

/**
  * Created by Bulat on 29.11.2016.
  */
class RouterActor(val conf: Config) extends Actor with RouterService{
  override def actorRefFactory = context

  override def receive = runRoute(routes)

  override def hostUrl: String = s"${conf.getString("mist.host")}:${conf.getString("mist.port")}"
}

trait RouterService extends HttpService {
  import scala.collection.JavaConversions._
  def hostUrl :String

  val routes =
    path("jobs") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            val sysProps = System.getProperties
            val routesConf = ConfigFactory.parseFile(new File("src/main/resources/routes.conf"))
            val root = routesConf.root()
            val routes = root.filter { x =>
              !sysProps.containsKey(x._1)
            }
            routes.keys.toList
          }
        }
      }
    } ~
      path("schedule") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              ("Hello", "My man")
            }
          }
        } ~
          post {
            entity(as[String]) { data =>
              respondWithMediaType(`application/json`) {
                complete(s"$data recieved")
              }
            }
          }
      }
}