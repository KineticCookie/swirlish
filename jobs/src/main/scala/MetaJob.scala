import io.hydrosphere.mist.lib.MistJob

object MetaJob extends MistJob {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")
    val ratio = parameters("posToNegRatio").asInstanceOf[String].toDouble
    val avg = parameters("avgTweetCount").asInstanceOf[String].toDouble
    val time = parameters("avgCalculationTime").asInstanceOf[String].toDouble

    val flag = (ratio < 5 || avg < 5 || time < 100)
    var text  = if (!flag) "Data is ok" else "Incorrect data"
    Map(
      "alert" -> flag,
      "data" -> text
    )
  }
}
