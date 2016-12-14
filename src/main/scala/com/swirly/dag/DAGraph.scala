package com.swirly.dag

import java.util.UUID

import spray.json.DefaultJsonProtocol

import scala.collection.mutable.ListBuffer

case class Node(uid: UUID, url: String) {
  override def toString: String = s"Node $uid //:$url"
}

case class Link(source: UUID, destination: UUID) {
  override def toString: String = s"Edge $source -> $destination"
}

case class DAGraph(nodes: Seq[Node], links: Seq[Link]) {
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
      nodes.filter(x => !destinations.contains(x.uid))
    }

    var S = getRoots().to[ListBuffer]
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

object DAGraphImplicits extends DefaultJsonProtocol {
  import com.swirly.utils.UuidJsonFormat._
  implicit def pairToLink(t : (Node, Node)) : Link = Link(t._1.uid, t._2.uid)
  implicit def seqPairToLink(s: Seq[(Node, Node)]): Seq[Link] =  s.map((t) => Link(t._1.uid, t._2.uid))

  implicit val nodeFormat = jsonFormat2(Node)
  implicit val linkFormat = jsonFormat2(Link)
  implicit val graphFromat = jsonFormat2(DAGraph)
}
