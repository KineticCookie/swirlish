package com.swirly

/**
  * Created by Bulat on 29.11.2016.
  */

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Boot extends App {
  val conf = ConfigFactory.load()

  val addr = Try{conf.getString("mist.host")}
  val port = Try{conf.getString("mist.port")}
  println(s"Mist server at $addr:$port")

  implicit val system = ActorSystem("swirlish")

  val service = system.actorOf(Props[RouterActor], "router")

  val timeoutDuration = Try {conf.getInt("spray.can.server.request-timeout")}
  implicit val timeout =  timeoutDuration match {
    case Success(value) => Timeout(value.seconds)
    case Failure(_) => Timeout(5.seconds)
  }

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
