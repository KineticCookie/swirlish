package com.swirl

import java.io.File

import com.typesafe.config.ConfigFactory

/**
  * Created by bulat on 26.12.16.
  */
object Configs {
  object Swirl {
    object Http {
      val host = "0.0.0.0"
      val port = 8080
    }
    val jobsTopic = "swirlish"
  }
  object Mist {
    val dockerFile = new File(Constants.Paths.Docker)
    val dockerConfig = ConfigFactory.parseFile(dockerFile)
    val conf = ConfigFactory.load(dockerConfig)

    object Mqtt {
      val host = conf.getString(Constants.Config.Mist.Mqtt.Host)
      val port = conf.getString(Constants.Config.Mist.Mqtt.Port)
      val subscribeTopic = conf.getString(Constants.Config.Mist.Mqtt.PublishTopic)//conf.getString(Constants.Config.Mist.Mqtt.SubscribeTopic)
      val publishTopic = conf.getString(Constants.Config.Mist.Mqtt.PublishTopic)
    }
  }
}
