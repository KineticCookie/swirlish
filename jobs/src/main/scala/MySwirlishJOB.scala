import java.io._

import io.hydrosphere.mist.lib.{MQTTPublisher, MistJob}
import org.apache.spark.streaming._


object MySwirlishJOB extends MistJob with MQTTPublisher {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")

    val ssc = new StreamingContext(context, Seconds(30))
    val lines = ssc.textFileStream("/")

    lines.foreachRDD((rdd) => {
      val collected: Array[String] = rdd.collect()

      var neg = 0
      var pos = 0
      var time = 0
      var cnt = 0

      var idx = 0
      while (idx < collected.length) {
        val x = collected(idx)
        val data = x.split(",")

        if (data.nonEmpty) {
          pos += data(2).toInt
          neg += data(3).toInt
          time += data(0).toInt
          cnt += data(1).toInt
        }

        idx += 1
      }

      val rddLen = collected.length
      val posToNegRatio = if (neg == 0) 0 else pos/neg
      val avgTweetCount = if (rddLen == 0) 0 else cnt/rddLen
      var avgCalculationTime = if (rddLen == 0) 0 else time/rddLen

      publish(Map(
        "rddLen" -> rddLen.toString,
        "positive" -> pos.toString,
        "negative" -> neg.toString,
        "cnt" -> cnt.toString,
        "time" -> time.toString
      ))
    })

    ssc.start()
    ssc.awaitTermination()
    Map.empty[String, Any]
  }
}
