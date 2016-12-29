import io.hydrosphere.mist.lib.MistJob


object RatioJob extends MistJob {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")

    val pos = parameters("positive").asInstanceOf[String].toDouble
    val neg = parameters("negative").asInstanceOf[String].toDouble

    val posToNegRatio = if (neg == 0) 0 else pos/neg
    Map(
      "posToNegRatio" -> posToNegRatio.toString
    )
  }
}
