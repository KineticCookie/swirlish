package com.swirly.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethod, HttpMethods, HttpRequest}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.swirly.data.JobResult
import com.swirly.messages.{JobFinished, StartJob}
import spray.json.pimpString
import spray.json._
/**
  * Created by Bulat on 02.12.2016.
  */
class JobActor(val id: UUID, val connectionString: String, val route: String) extends Actor {
  import com.swirly.utils.MapFormat._
  import com.swirly.data.JobResultImplicits._

  val log = Logging(context.system, this)
  val http = Http(context.system)
  override def receive: Receive = {
    case StartJob(data) =>
      log.debug(s"JobActor[$id]($route@$connectionString) Started")
      var senderOriginal = sender()
      implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
      implicit val ex = context.dispatcher
      val resp = http.singleRequest(
        HttpRequest(
          uri = s"http://$connectionString/api/route",
          method = HttpMethods.POST,
          entity = data.toJson.toString
        )
      )
      resp.map { resp =>
        var result = resp.entity.toString.parseJson.convertTo[JobResult]
        senderOriginal ! JobFinished(id, result.payload)
      }
    case x => log.debug(s"Unidentified message: $x")
  }
}
