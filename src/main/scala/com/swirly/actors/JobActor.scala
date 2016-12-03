package com.swirly.actors

import akka.actor.Actor
import akka.event.Logging
import com.swirly.messages.StartJob

import scala.util.Random

/**
  * Created by Bulat on 02.12.2016.
  */
class JobActor extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case StartJob =>
      log.debug("Start job recieved")

      var sum = 0
      for(x <- 0 to 1000) {
        sum += Random.nextInt(100)
      }
      log.debug(s"Job result: $sum")
      sender() ! sum

    case x => log.debug(s"Unidentified message: $x")
  }
}
