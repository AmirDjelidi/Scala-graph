trait Edge[A] {
  def weight: Int
}

case class EdgeDirected[A](from: A, to: A, weight: Int) extends Edge[A]

case class EdgeUndirected[A](node1: A, node2: A, weight: Int) extends Edge[A]
trait Graph[A] {
  type SubTypeEdge <: Edge[A]
  def vertices: Set[A]
  def edges: List[Edge[A]]
  def addEdge(newEdge: SubTypeEdge): Graph[A]
  def removeEdge(edgeToRemove: SubTypeEdge): Graph[A]
}

case class GraphDirected[A](adjacenceMatrix : Map[A, Set[A]] = Map.empty) extends Graph[A] {
  override type SubTypeEdge = EdgeDirected[A]
  def vertices: Set[A] = adjacenceMatrix.keySet
  def edges = {
    adjacenceMatrix.toList.flatMap {
      case (from, tos) =>
        tos.map(to => EdgeDirected(from, to, weight = 1))
    }
  }
  def addEdge(newEdge: SubTypeEdge): Graph[A] = {
    val from : A = newEdge.from
    val to : A = newEdge.to
    val updatedNeighbours: Set[A] = adjacenceMatrix.getOrElse(from, Set()) + to
    val newMatrix = adjacenceMatrix + (from -> updatedNeighbours)
    GraphDirected(newMatrix)
  }

  def removeEdge(edgeToRemove: SubTypeEdge): Graph[A] = {
    val from : A = edgeToRemove.from
    val to : A = edgeToRemove.to
    val updatedNeighbours : Set[A] = adjacenceMatrix.getOrElse(from, Set()) - to
    val newMatrix = updatedNeighbours match {
      case s if s.isEmpty => adjacenceMatrix - from
      case s => adjacenceMatrix + (from -> s)
    }
    GraphDirected(newMatrix)
  }

  def displayAdjacencyMatrix(): Unit = {
    println("Adjacency Matrix:")
    val header = (" " * 5)
    println(header)

    for (from <- this.vertices) {
      print (f"$from " )
      val neighborHood = this.adjacenceMatrix.getOrElse(from, Set())
      for (to <- this.vertices){
        if (neighborHood.contains(to)){
          print(1)
        }
        else{
          print(0)
        }
      }
      println()
    }
  }
}

case class GraphUnDirected[A](adjacenceMatrix : Map[A, Set[A]]) extends Graph[A] {
  override type SubTypeEdge <: EdgeUndirected[A]
  def addEdge (newEdge: SubTypeEdge): Graph[A] = {
    val node1 : A = newEdge.node1;
    val node2 : A = newEdge.node2;
    val neighborHood1: Set[A] = this.adjacenceMatrix.getOrElse(node1, Set()) + node2;
    val neighborHood2 : Set[A] = this.adjacenceMatrix.getOrElse(node2, Set()) + node1;
    val newMatrix = this.adjacenceMatrix + (node1 -> neighborHood1) + (node2 -> neighborHood2)
    GraphUnDirected(newMatrix)
  }

  def removeEdge (edgeToRemove: SubTypeEdge): Graph[A] = {
    val node1: A = edgeToRemove.node1;
    val node2: A = edgeToRemove.node2;
    val neighborHood1: Set[A] = this.adjacenceMatrix.getOrElse(node1, Set()) - node2;
    val neighborHood2: Set[A] = this.adjacenceMatrix.getOrElse(node2, Set()) - node1;
    val newMatrix1 = neighborHood1 match {
      case n if n.isEmpty => this.adjacenceMatrix - node1
      case n => this.adjacenceMatrix + (node1 -> neighborHood1)
    }
    val newMatrix2 = neighborHood2 match {
      case n if n.isEmpty => newMatrix1 - node1
      case n => newMatrix1 + (node2 -> neighborHood2)
    }
    GraphDirected(newMatrix2)
  }
  def vertices: Set[A] = this.adjacenceMatrix.keySet

  override def edges: List[Edge[A]] = {
    adjacenceMatrix.toList.flatMap {
      case (node1, tos) =>
        tos.map(node2 => EdgeUndirected(node1, node2, weight = 1))
    }
  }
}
val g = GraphDirected(Map("A" -> Set("B"), "B" -> Set("C"), "C" -> Set("A","B")))
g.displayAdjacencyMatrix()
val  edge : EdgeDirected[String] = EdgeDirected("A", "C", 1)
g.addEdge(edge)
print(g.edges)
