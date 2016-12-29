import io.hydrosphere.mist.lib.MistJob


object CalcTimeJob extends MistJob {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")

    val time = parameters("time").asInstanceOf[String].toDouble
    val rddLen = parameters("rddLen").asInstanceOf[String].toDouble

    var avgCalculationTime = if (rddLen == 0) 0 else time/rddLen
    Map(
      "avgCalculationTime" -> avgCalculationTime.toString
    )
  }
}
