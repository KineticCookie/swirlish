package com.swirl.data.dag

import java.util.UUID

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by Bulat on 09.01.2017.
  */
case class Node(uid: UUID, url: String) {
  override def toString: String = s"Node $uid //:$url"
}

object Node extends DefaultJsonProtocol {

  import com.swirl.utils.json.UuidJsonFormat._

  implicit val nodeFormat: RootJsonFormat[Node] = jsonFormat2(Node.apply)
}