package graph
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

  protected def displayAdjacencyMatrix(adjacenceMatrix: Map[A, Set[(A, Int)]]): Unit = {
    println("Adjacency Matrix:")
    val header = (" " * 5)
    println(header)

    for (from <- this.vertices) {
      print(f"$from ")
      val neighborHood = adjacenceMatrix.getOrElse(from, Set())
      for (to <- this.vertices) {
        val weight = neighborHood.find((_._1 == to)).map(_._2);
        print(weight.map(_.toString).getOrElse("oo").reverse.padTo(5, ' ').reverse)

      }
      println()
    }
  }
}

case class GraphDirected[A](adjacenceMatrix: Map[A, Set[(A, Int)]] = Map.empty) extends Graph[A] {
  override type SubTypeEdge = EdgeDirected[A]

  def vertices: Set[A] = adjacenceMatrix.keySet

  def edges = for {
    (from, tos) <- adjacenceMatrix.toList
    (to, weight) <- tos
  } yield EdgeDirected(from, to, weight)

  def addEdge(newEdge: SubTypeEdge): GraphDirected[A] = {
    val from: A = newEdge.from
    val to: A = newEdge.to
    val weight: Int = newEdge.weight
    val updatedNeighbours: Set[(A, Int)] = adjacenceMatrix.getOrElse(from, Set()) + ((to, weight))
    val newMatrix: Map[A, Set[(A, Int)]] = adjacenceMatrix + (from -> updatedNeighbours)
    GraphDirected(newMatrix)
  }

  def removeEdge(edgeToRemove: SubTypeEdge): GraphDirected[A] = {
    val from: A = edgeToRemove.from
    val to: A = edgeToRemove.to
    val weight: Int = edgeToRemove.weight
    val updatedNeighbours: Set[(A, Int)] = adjacenceMatrix.getOrElse(from, Set()) - ((to, weight))
    val newMatrix = updatedNeighbours match {
      case s if s.isEmpty => adjacenceMatrix - from
      case s => adjacenceMatrix + (from -> s)
    }
    GraphDirected(newMatrix)
  }

  def displayAdjacencyMatrix(): Unit = {
    this.displayAdjacencyMatrix(this.adjacenceMatrix)
  }
}

case class GraphUnDirected[A](adjacenceMatrix: Map[A, Set[(A, Int)]]) extends Graph[A] {
  override type SubTypeEdge = EdgeUndirected[A]

  def vertices: Set[A] = this.adjacenceMatrix.keySet

  def edges: List[EdgeUndirected[A]] = for {
    (from, tos) <- adjacenceMatrix.toList
    (to, weight) <- tos
  } yield EdgeUndirected(from, to, weight)

  def addEdge(newEdge: SubTypeEdge): GraphUnDirected[A] = {
    val node1: A = newEdge.node1;
    val node2: A = newEdge.node2;
    val weight: Int = newEdge.weight;
    val neighborHood1: Set[(A, Int)] = this.adjacenceMatrix.getOrElse(node1, Set()) + ((node2, weight));
    val neighborHood2: Set[(A, Int)] = this.adjacenceMatrix.getOrElse(node2, Set()) + ((node1, weight));
    val newMatrix = this.adjacenceMatrix + (node1 -> neighborHood1) + (node2 -> neighborHood2)
    GraphUnDirected(newMatrix)
  }

  def removeEdge(edgeToRemove: SubTypeEdge): GraphUnDirected[A] = {
    val node1: A = edgeToRemove.node1;
    val node2: A = edgeToRemove.node2;
    val weight: Int = edgeToRemove.weight;
    val neighborHood1: Set[(A, Int)] = this.adjacenceMatrix.getOrElse(node1, Set()) - ((node2, weight));
    val neighborHood2: Set[(A, Int)] = this.adjacenceMatrix.getOrElse(node2, Set()) - ((node1, weight));
    val newMatrix1 = neighborHood1 match {
      case n if n.isEmpty => this.adjacenceMatrix - node1
      case n => this.adjacenceMatrix + (node1 -> neighborHood1)
    }
    val newMatrix2 = neighborHood2 match {
      case n if n.isEmpty => newMatrix1 - node1
      case n => newMatrix1 + (node2 -> neighborHood2)
    }
    GraphUnDirected(newMatrix2)
  }

  def displayAdjacencyMatrix(): Unit = {
    this.displayAdjacencyMatrix(this.adjacenceMatrix)
  }
}

object GraphUnDirected {
  def symmetricMatrix[A](graph: GraphUnDirected[A], indexNode : Int = 0, indexNeighborhood : Int = 0): GraphUnDirected[A] = {
    val node1 : A = graph.vertices.toList(indexNode)
    val neighborHood = graph.adjacenceMatrix.getOrElse(node1, Set())
    val nodes = neighborHood.map((node, weight) => node).toList
    val node2 = nodes(indexNeighborhood)
    print(node1, node2)
    if (node1 == graph.vertices.last) {
      return graph
    }
    val weight = neighborHood.find((_._1 == node2)).map(_._2).fold(ifEmpty = 0)(f = value => value);
    val graph2 = graph.addEdge(EdgeUndirected(node2, node1, weight))
    if (indexNeighborhood < nodes.length - 1) {
      symmetricMatrix(graph2, indexNode, indexNeighborhood + 1)
    }
    else {
      symmetricMatrix(graph2, indexNode + 1, 0)
    }
  }

  def main(args: Array[String]): Unit = {
    val g = GraphUnDirected(Map("A" -> Set(("C", 2), ("D", 3)), "B" -> Set(("C", 4)), "C" -> Set(), "D" -> Set()))
    val g2 = GraphUnDirected.symmetricMatrix(g)
    val g3 = g2.addEdge(EdgeUndirected("B", "D", 5))
    g3.displayAdjacencyMatrix()

  }

}