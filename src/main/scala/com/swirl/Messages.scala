package com.swirl

import java.util.UUID

import akka.actor.ActorRef
import com.swirl.data.{JobRequest, JobResult}

/**
  * Created by bulat on 16.12.16.
  */
object Messages {

  object MqttActor {

    case class JobFinished(id: UUID, result: JobResult)

    case class StreamData(payload: Map[String, Any])

    case class UpdateGraph(graphActor: ActorRef)

  }

  object GraphActor {

    case class StartJob(jobRequest: JobRequest)

    case class GetGraph()

    case class GetHistory(jobId: UUID)

  }

}