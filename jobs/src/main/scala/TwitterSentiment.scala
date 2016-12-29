/**
  * Created by iskandar on 27/12/16.
  */

import java.io.{File, FileOutputStream, PrintWriter}

import io.hydrosphere.mist.lib.{MQTTPublisher, MistJob}
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import twitter4j.Status

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayesModel

object TwitterSentiment extends MistJob with MQTTPublisher {
  override def doStuff(parameters: Map[String, Any]): Map[String, Any] = {
    context.setLogLevel("INFO")

    val ssc = new StreamingContext(context, Seconds(10))
    val stream = TwitterUtils.createStream(ssc, None, Array("#usa"))
    val model = NaiveBayesModel.load(context, "/models/NaiveBayes")
    val hashingTF = new HashingTF(1000)

    stream.foreachRDD { (rdd) =>
      val collected: Array[Status] = rdd.collect()
      var idx = 0

      var pos = 0
      var neg = 0
      var neutral = 0

      val pw = new PrintWriter(new FileOutputStream(new File(s"/${rdd.id}log.txt"), true))

      val t0 = System.nanoTime()
      while (idx < collected.length) {
        val x = collected(idx)

        val input = new LabeledPoint(1, hashingTF.transform(x.getText.sliding(2).toSeq)).features
        val sentiment = model.predict(input)

        sentiment match {
          case 4.0 => pos += 1
          case 2.0 => neutral += 1
          case 0.0 => neg += 1
        }

        idx += 1
      }

      val t1 = System.nanoTime()
      val time = (t1 - t0)/1000

      pw.write(s"$time,${collected.length},$pos,$neg,$neutral")
      pw.flush()
      pw.close()
    }

    ssc.start()
    ssc.awaitTermination()

    Map.empty[String, Any]
  }
}
