import java.security.MessageDigest

import io.hydrosphere.mist.lib.{MQTTPublisher, MistJob}
import org.apache.spark.streaming._

import scala.util.Random

object MyJob extends MistJob with MQTTPublisher {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")
    println("Started!")
    val ssc = new StreamingContext(context, Seconds(30))
    while(true) {
      var l = "test"
      for (x <- 0 to 100000) {
        l = md5(l)
      }
      publish(Map("number" -> Random.nextInt(255).toString))
    }
    ssc.start()
    ssc.awaitTermination()
    Map.empty[String, Any]
  }

  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString
  }
}
