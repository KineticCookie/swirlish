package com.swirly.actors

import java.util.UUID

import akka.actor.Actor
import akka.event.Logging
import com.swirly.messages._
/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor extends Actor {
  val log = Logging(context.system, this)
  log.info("GraphActor created")

  override def receive: Receive = {
    case StartGraph =>
      log.info(s"Start graph recieved")

    case GetCurrentJob =>
      val id = UUID.randomUUID
      log.info(s"GetCurrentJob returns $id")
      sender ! id
  }
}
