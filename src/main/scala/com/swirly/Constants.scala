package com.swirly

/**
  * Created by bulat on 21.12.16.
  */
object Constants {
  final var StringEncoding = "utf-8"

  object Actors {
    final val ActorSystem = "Swirlish"
    final val Graph = "GraphActor"
    final val Job = "JobActor"
    final val Mqtt = "MqttActor"
    final val StreamListener= "StreamListenerActor"
  }

  object Paths {
    final val Docker = "configs/docker.conf"
    final val Routes = "configs/router.conf"
  }

  object Config {
    object Mist {
      object Mqtt {
        final val Host = "mist.mqtt.host"
        final val Port = "mist.mqtt.port"
        final val SubscribeTopic = "mist.mqtt.subscribe-topic"
        final val PublishTopic = "mist.mqtt.publish-topic"
      }

      object Http {
        final val Host = "mist.http.host"
        final val Port = "mist.http.port"
      }
    }
  }
}
