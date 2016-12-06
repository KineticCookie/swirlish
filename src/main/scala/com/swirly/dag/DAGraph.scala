package com.swirly.dag

import java.util.UUID
import scala.collection.mutable.ListBuffer

class Node(val uid: UUID, val url: String) {

  override def toString: String = "Node uid:" + uid + " url:" + url
}

class Link(val source: String, val destination: String) {

  override def toString: String = "Edge " + source + " -> " + destination
}

class DAGraph(val nodes: Seq[Node], val links: Seq[Link]) {
  def in (node: Node): Seq[Link] = ???
  def out(node: Node): Seq[Link] = ???

  /**
    * returns true if graph has no cycles;
    * it also sorts graph in topological order;
    *
    * @return Boolean
    */
  def Kahn(): Boolean = {
    def getRoots(): Seq[Node] = {
      val destinations = links.map(_.destination)
      nodes.filter(x => !destinations.contains(x.url))
    }

    var S = getRoots().to[ListBuffer]
    var edges = links.to[ListBuffer]
    val L = new ListBuffer[Node]()

    while (S.nonEmpty) {
      val n = S.head
      S -= n
      L += n

      for (e <- edges.filter(x => x.source == n.url)) {
        val m = e.destination
        edges -= e
        if (!edges.exists(x => x.destination == m)) S += nodes.filter(x => x.url == m).head
      }
    }

    edges.isEmpty
  }
}

//// TODO: tests
//object Test extends App {
//  val node1 = new Node(UUID.randomUUID(), "url1")
//  val node2 = new Node(UUID.randomUUID(), "url2")
//  val node3 = new Node(UUID.randomUUID(), "url3")
//  val node4 = new Node(UUID.randomUUID(), "url4")
//  val nodes = Seq(node1, node2, node3, node4)
//  val edges = Seq(
//    new Link(node1.url, node2.url),
//    new Link(node2.url, node4.url),
//    new Link(node3.url, node4.url),
//    new Link(node3.url, node2.url),
//    new Link(node1.url, node3.url)
//  )
//  val graph = new DAGraph(nodes, edges)
//  print(graph.Kahn())
//}

