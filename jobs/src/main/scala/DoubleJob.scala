import io.hydrosphere.mist.lib.MistJob

/**
  * Created by bulat on 20.12.16.
  */
object DoubleJob extends MistJob {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    val number = parameters("number").asInstanceOf[String].toDouble * 2
    Map("number" -> number.toString)
  }
}
