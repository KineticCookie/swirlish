package com.swirly.dag

import java.util.UUID
import scala.collection.mutable.ListBuffer

class Node(val uid: UUID, val url: String) {
  override def toString: String = "Node uid:" + uid + " url:" + url
}

class Link(val source: String, val destination: String) {
  def this(src: Node, dst: Node) = this(src.url, src.url)

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

object DAGraph {
  implicit def pairToLink(t : (Node, Node)) : Link = new Link(t._1, t._2)
  implicit def seqPairToLink(s: Seq[(Node, Node)]): Seq[Link] =  s.map((t) => new Link(t._1.url, t._2.url))
}