package dag

import java.util.UUID

import org.scalatest._
import com.swirl.data.{DAGraph, Node}
/**
  * Created by bulat on 11.12.16.
  */
class DAGraphTests extends FlatSpec with Matchers{
  import com.swirl.data.DAGraphImplicits._

  "Kahn algorithm" should "return true for DAG" in {
    val node1 = Node(UUID.randomUUID(), "url1")
    val node2 = Node(UUID.randomUUID(), "url2")
    val node3 = Node(UUID.randomUUID(), "url3")
    val node4 = Node(UUID.randomUUID(), "url4")
    val nodes = Seq(node1, node2, node3, node4)
    val edges = Seq(
      node1 -> node2,
      node2 -> node4,
      node3 -> node4,
      node3 -> node2,
      node1 -> node3
    )
    val graph = DAGraph(nodes, edges)
    graph.kahn() shouldBe true
  }

  it should "return false for graph with cycles" in {
    val node1 = Node(UUID.randomUUID(), "url1")
    val node2 = Node(UUID.randomUUID(), "url2")
    val node3 = Node(UUID.randomUUID(), "url3")
    val node4 = Node(UUID.randomUUID(), "url4")
    val nodes = Seq(node1, node2, node3, node4)
    val edges = Seq(
      node1 -> node2,
      node2 -> node3,
      node3 -> node4,
      node4 -> node2
    )
    val graph = DAGraph(nodes, edges)
    graph.kahn() shouldBe false
  }
}