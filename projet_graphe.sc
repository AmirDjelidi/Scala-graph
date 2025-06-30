trait Edge[A] {
  def weight: Int
}
case class EdgeDirected[A](from : A, to : A, weight : Int) extends Edge[A]
case class EdgeUndirected[A](node1 : A, node2 : A, weight : Int) extends Edge[A]
val edge = EdgeDirected(1,2,3)

trait Graph[A] {
  def vertices[A] : List[A]  //unique éléments
  def addEdge[A](newEdge : Edge[A]) : Graph[A]
  def removeEdge[A](edgeToRemove: Edge[A]): Graph[A]
}
case class GraphDirected[A]() extends Graph[A]{
  override def addEdge[A](newEdge: EdgeDirected[A]): Graph[A]
}

case class GraphUnDirected[A]() extends Graph[A]{
  override def addEdge[A](newEdge: EdgeUndirected[A]): Graph[A]
}

