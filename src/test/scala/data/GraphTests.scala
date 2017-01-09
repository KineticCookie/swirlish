package data

import java.util.UUID

import org.scalatest._
import com.swirl.data.dag.{Graph, Node}
/**
  * Created by bulat on 11.12.16.
  */
class GraphTests extends FlatSpec with Matchers {

  "Graph" should "know output links for given node" in {
    val uuid1 = UUID.randomUUID()
    val uuid2 = UUID.randomUUID()
    val uuid3 = UUID.randomUUID()
    val uuid4 = UUID.randomUUID()
    val node1 = Node(uuid1, "1")
    val node2 = Node(uuid2, "2")
    val node3 = Node(uuid3, "3")
    val node4 = Node(uuid4, "1")
    val nodes = Seq(node1, node2, node3, node4)
    val edges = Seq(
      node1 -> node2,
      node2 -> node3,
      node2 -> node4,
      node3 -> node4,
      node1 -> node4
    )
    val graph = Graph(nodes, edges)

    graph.in(node1) shouldBe empty
    graph.in(node2).map(_.source) should contain only node1.uid
    graph.in(node3).map(_.source) should contain only node2.uid
    graph.in(node4).map(_.source) should contain allOf(node1.uid, node2.uid, node3.uid)
  }

  it should "know input links for given node" in {
    val uuid1 = UUID.randomUUID()
    val uuid2 = UUID.randomUUID()
    val uuid3 = UUID.randomUUID()
    val uuid4 = UUID.randomUUID()
    val node1 = Node(uuid1, "1")
    val node2 = Node(uuid2, "2")
    val node3 = Node(uuid3, "3")
    val node4 = Node(uuid4, "1")
    val nodes = Seq(node1, node2, node3, node4)
    val edges = Seq(
      node1 -> node2,
      node2 -> node3,
      node2 -> node4,
      node3 -> node4,
      node1 -> node4
    )
    val graph = Graph(nodes, edges)

    graph.out(node1).map(_.destination) should contain allOf (node2.uid, node4.uid)
    graph.out(node2).map(_.destination) should contain allOf (node3.uid, node4.uid)
    graph.out(node3).map(_.destination) should contain only node4.uid
    graph.out(node4) shouldBe empty
  }

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
    val graph = Graph(nodes, edges)
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
    val graph = Graph(nodes, edges)
    graph.kahn() shouldBe false
  }
}