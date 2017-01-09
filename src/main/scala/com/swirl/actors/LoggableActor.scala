package com.swirl.actors

import akka.actor.Actor
import akka.event.Logging

/**
  * Created by Bulat on 09.01.2017.
  */
abstract class LoggableActor extends Actor {
  val log = Logging(context.system, this)
}
