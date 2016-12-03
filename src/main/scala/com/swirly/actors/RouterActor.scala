package com.swirly.actors

import akka.actor.Actor
import com.typesafe.config.Config
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
  def hostUrl :String

  val routes =
    path("jobs"){
      get {
        respondWithMediaType(`application/json`) {
          complete {
            Map(
              "Tweets" -> s"$hostUrl/tweets",
              "Stats" -> s"$hostUrl/stats",
              "Training" -> s"$hostUrl/train"
            )
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