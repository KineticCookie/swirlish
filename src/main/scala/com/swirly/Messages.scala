package com.swirly.messages

import java.util.UUID

import com.swirly.data.DAGraph

/**
  * Created by bulat on 16.12.16.
  */
case class GetCurrentJob()
case class StartGraph()
case class StartJob(data: Map[String, Any])
case class JobFinished(id: UUID, result: Map[String, Any])
case class UpdateGraph(dAGraph: DAGraph)