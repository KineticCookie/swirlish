package com.swirl.data.dag

import java.util.UUID

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by Bulat on 09.01.2017.
  */
case class Link(source: UUID, destination: UUID) {
  override def toString: String = s"Edge $source -> $destination"
}

object Link extends DefaultJsonProtocol {

  import com.swirl.utils.json.UuidJsonFormat._

  implicit def pairToLink(t: (Node, Node)): Link = Link(t._1.uid, t._2.uid)

  implicit def seqPairToLink(s: Seq[(Node, Node)]): Seq[Link] = s.map((t) => Link(t._1.uid, t._2.uid))

  implicit val linkFormat: RootJsonFormat[Link] = jsonFormat2(Link.apply)
}