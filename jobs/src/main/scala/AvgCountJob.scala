import io.hydrosphere.mist.lib.MistJob


object AvgCountJob extends MistJob {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")

    val cnt = parameters("cnt").asInstanceOf[String].toDouble
    val rddLen = parameters("rddLen").asInstanceOf[String].toDouble

    val avgTweetCount = if (rddLen == 0) 0 else cnt/rddLen
    Map(
      "avgTweetCount" -> avgTweetCount.toString
    )
  }
}
