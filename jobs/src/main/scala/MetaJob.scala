import io.hydrosphere.mist.lib.MistJob

object MetaJob extends MistJob {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")

    val ratio = parameters("posToNegRatio").asInstanceOf[String].toDouble
    val avg = parameters("avgTweetCount").asInstanceOf[String].toDouble
    val time = parameters("avgCalculationTime").asInstanceOf[String].toDouble

    Map(
      "alert" -> (ratio == 5 || avg == 10 || time == 100)
    )
  }
}
