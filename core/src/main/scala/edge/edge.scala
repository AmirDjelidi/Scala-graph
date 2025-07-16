package edge

trait Edge[A] {
  def weight: Int
}

case class EdgeDirected[A](from: A, to: A, weight: Int) extends Edge[A]

case class EdgeUndirected[A](node1: A, node2: A, weight: Int) extends Edge[A]