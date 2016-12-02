package com.swirly.dag
import java.util.UUID

/**
  * Created by Bulat on 29.11.2016.
  */

class Node[T](val id: UUID, val data: T) {

}

class Link[T](val source: Node[T], val destination: Node[T]) {

}

class DAGraph[T](val nodes: Seq[Node[T]], val links: Seq[Link[T]]) {

  def in (node: Node[T]): Seq[Link[T]] = ???
  def srcs(node: Node[T]): Seq[Node[T]] = ???

  def out(node: Node[T]): Seq[Link[T]] = ???
  def dests(node: Node[T]): Seq[Node[T]] = ???
}
