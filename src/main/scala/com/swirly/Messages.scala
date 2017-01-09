package com.swirly.messages

import java.util.UUID

import com.swirly.data.{DAGraph, JobResult}

/**
  * Created by bulat on 16.12.16.
  */
case class StartJob(data: Map[String, Any])
case class JobFinished(id: UUID, result: JobResult)
case class UpdateGraph(dAGraph: DAGraph)
case class StreamData(payload: Map[String, Any])
case class GetGraph()
case class GetHistory(jobId: UUID)
