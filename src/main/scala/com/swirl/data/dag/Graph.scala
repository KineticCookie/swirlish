package com.swirl.data.dag

import java.util.UUID

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.collection.mutable.ListBuffer

case class Graph(nodes: Seq[Node], links: Seq[Link]) {
  def in (node: Node): Seq[Link] = in(node.uid)
  def out(node: Node): Seq[Link] = out(node.uid)

  def in(id: UUID): Seq[Link] = links.filter(_.destination == id)
  def out(id: UUID): Seq[Link] = links.filter(_.source == id)

  def roots: Seq[Node] = {
    if(links.nonEmpty) {
      val destinations = links.map(_.destination)
      nodes.filter(x => !destinations.contains(x.uid))
    } else {
      nodes
    }
  }

  def root: Node = roots.head

  /**
    * returns true if graph has no cycles;
    * it also sorts graph in topological order;
    *
    * @return Boolean
    */
  def kahn(): Boolean = {
    var S = roots.to[ListBuffer]
    var edges = links.to[ListBuffer]
    val L = new ListBuffer[Node]()

    while (S.nonEmpty) {
      val n = S.head
      S -= n
      L += n

      for (e <- edges.filter(x => x.source == n.uid)) {
        val m = e.destination
        edges -= e
        if (!edges.exists(x => x.destination == m)) S += nodes.filter(x => x.uid == m).head
      }
    }

    edges.isEmpty
  }
}

object Graph extends DefaultJsonProtocol {

  import com.swirl.utils.json.UuidJsonFormat._

  implicit val nodeFormat: RootJsonFormat[Node] = jsonFormat2(Node.apply)
  implicit val linkFormat: RootJsonFormat[Link] = jsonFormat2(Link.apply)
  implicit val graphFormat: RootJsonFormat[Graph] = jsonFormat2(Graph.apply)
}
