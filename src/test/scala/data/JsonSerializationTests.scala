package data

import java.util.UUID

import com.swirl.data.{FullJobConfiguration, HistoryData, JobRequest, JobResult}
import com.swirl.data.dag.{Graph, Node}
import com.swirl.utils.Time
import org.scalatest.{FlatSpec, Matchers}
/**
  * Created by Bulat on 09.01.2017.
  */
class JsonSerializationTests extends FlatSpec with Matchers {
  import spray.json._

  "DAGraph" should "be serializeable to JSON" in {
    val node1 = Node(UUID.randomUUID(), "sum")
    val node2 = Node(UUID.randomUUID(), "square")
    val node3 = Node(UUID.randomUUID(), "double")
    val nodes = Seq(node1, node2, node3)
    val edges = Seq(
      node1 -> node2,
      node2 -> node3
    )
    val g = Graph(nodes, edges)
    g.toJson.compactPrint != "" shouldBe true
  }

  "FullJobConfiguration" should "be serializeable to JSON" in {
    val conf = FullJobConfiguration(
      "testPath",
      "testClass$",
      "testNamespace",
      Map("key1" -> 1, "key2" -> true),
      Some("id")
    )

    conf.toJson.compactPrint != "" shouldBe true
  }

  "HistoryData" should "be serializeable to JSON" in {
    val data = HistoryData(
      Time.unixNow,
      Map("key1" -> 1, "key2" -> true)
    )

    data.toJson.compactPrint != "" shouldBe true
  }

  "JobRequest" should "be serializeable to JSON" in {
    val req = JobRequest(
      "testRoute",
      Map("key1" -> 1, "key2" -> true),
      Some("id")
    )

    req.toJson.compactPrint != "" shouldBe true
  }

  "JobResult" should "be serializeable to JSON" in {
    val conf = FullJobConfiguration(
      "testPath",
      "testClass$",
      "testNamespace",
      Map("key1" -> 1, "key2" -> true),
      Some("id")
    )
    val res = JobResult(
      true,
      Map("key1" -> 1, "key2" -> true),
      List.empty,
      conf
    )

    res.toJson.compactPrint != "" shouldBe true
  }
}
