package com.swirly.actors

import akka.actor.Actor
import akka.event.Logging
import com.swirly.messages._
/**
  * Created by Bulat on 02.12.2016.
  */
class GraphActor extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case StartGraph =>
      log.debug(s"Start graph recieved")

    case GetCurrentJob(userId) =>
      log.debug(s"GetCurrentJob for user($userId)")
  }
}
