package com.swirly

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.http._
import spray.routing._
import spray.json._
import spray.can.Http
import spray.httpx.SprayJsonSupport._
import DefaultJsonProtocol._

/**
  * Created by Bulat on 29.11.2016.
  */
class RouterActor extends Actor with RouterService{
  override def actorRefFactory = context

  override def receive = runRoute(routes)
}

trait RouterService extends HttpService {
  val routes =
    path("schedule") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            Map("asd" -> "asd")
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