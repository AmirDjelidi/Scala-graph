package graph

import scala.annotation.tailrec

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
    val header = " " * 5
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

  def dijkstra(start: A, destination: A, adjacenceMatrix: Map[A, Set[(A, Int)]]): (List[A], Int) = {
    @tailrec
    def djikstraHelper(
                        currentNode: A,
                        path: List[A],
                        size: Int,
                        otherWays: List[(List[A], Int)]
                      ): (List[A], Int) = {

      if (currentNode == destination) {
        return (path, size)
      }
      val neighborhood = adjacenceMatrix.getOrElse(currentNode, Set()).toList
        .filterNot { case (node, _) => path.contains(node) }
        .map { case (node, weight) => (path.appended(node), weight + size) }
      val allWays = (neighborhood ++ otherWays)
      val (newPath, nextSize) = allWays.minBy(_._2)
      djikstraHelper(newPath.last, newPath, nextSize, allWays.filterNot(_._1 == newPath))
    }

    djikstraHelper(start, List(start), 0, List())
  }

  def DFS(): Boolean

  def BFS(start: A): Set[A]

  def hasCycle(): Boolean
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

  def dijkstra(start: A, destination: A): (List[A], Int) = dijkstra(start, destination, this.adjacenceMatrix)

  override def DFS(): Boolean = {
    def dfsHelper(start: A): Set[A] = {
      @tailrec
      def loop(stack: List[A], visited: Set[A]): Set[A] = {
        stack match {
          case Nil => visited
          case node :: rest if visited.contains(node) => loop(rest, visited)
          case node :: rest =>
            val neighbors = adjacenceMatrix.getOrElse(node, Set()).map(_._1)
            val newNodes = neighbors.diff(visited)
            loop(newNodes.toList ++ rest, visited + node)
        }
      }

      loop(List(start), Set())
    }

    vertices.forall(start => dfsHelper(start).size == vertices.size)
  }

  def BFS(start: A): Set[A] = {
    @tailrec
    def bfsHelper(queue: List[A], visited: Set[A]): Set[A] = {
      queue match {
        case Nil => visited
        case node :: rest if visited.contains(node) =>
          bfsHelper(rest, visited)
        case node :: rest =>
          val neighbors = adjacenceMatrix.getOrElse(node, Set()).map(_._1)
          val unvisited = neighbors.diff(visited)
          bfsHelper(rest ++ unvisited.toList, visited + node)
      }
    }

    bfsHelper(List(start), Set())
  }


  def hasCycle(): Boolean = {
    def dfs(node: A, visited: Set[A], recStack: Set[A]): Boolean = {
      if (recStack.contains(node)) return true
      if (visited.contains(node)) return false

      val neighbors = adjacenceMatrix.getOrElse(node, Set()).map(_._1)
      neighbors.exists(n => dfs(n, visited + node, recStack + node))
    }

    vertices.exists(node => dfs(node, Set(), Set()))
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

  def dijkstra(start: A, destination: A): (List[A], Int) = dijkstra(start, destination, this.adjacenceMatrix)


  def displayAdjacencyMatrix(): Unit = {
    this.displayAdjacencyMatrix(this.adjacenceMatrix)
  }

  override def DFS(): Boolean = {
    @tailrec
    def dfsHelper(stack: List[A], visited: Set[A]): Set[A] = {
      stack match {
        case Nil => visited
        case node :: rest if visited.contains(node) =>
          dfsHelper(rest, visited)
        case node :: rest =>
          val neighbors = adjacenceMatrix.getOrElse(node, Set()).map(_._1)
          val newNodes = neighbors.diff(visited)
          dfsHelper(newNodes.toList ++ rest, visited + node)
      }
    }

    if (vertices.isEmpty) return true
    val start = vertices.head
    val visited = dfsHelper(List(start), Set())
    visited.size == vertices.size
  }

  def BFS(start: A): Set[A] = {
    @tailrec
    def bfsHelper(queue: List[A], visited: Set[A]): Set[A] = {
      queue match {
        case Nil => visited
        case node :: rest if visited.contains(node) =>
          bfsHelper(rest, visited)
        case node :: rest =>
          val neighbors = adjacenceMatrix.getOrElse(node, Set()).map(_._1)
          val unvisited = neighbors.diff(visited)
          bfsHelper(rest ++ unvisited.toList, visited + node)
      }
    }

    bfsHelper(List(start), Set())
  }

  def hasCycle(): Boolean = {
    def dfs(node: A, visited: Set[A], parent: Option[A]): Boolean = {
      val neighbors = adjacenceMatrix.getOrElse(node, Set()).map(_._1)
      for (neighbor <- neighbors) {
        if (!visited.contains(neighbor)) {
          if (dfs(neighbor, visited + neighbor, Some(node))) return true
        } else if (Some(neighbor) != parent) {
          return true
        }
      }
      false
    }
    vertices.exists(start => dfs(start, Set(start), None))
  }

}

object GraphUnDirected {
  @tailrec
  def symmetricMatrix[A](graph: GraphUnDirected[A], indexNode: Int = 0, indexNeighborhood: Int = 0): GraphUnDirected[A] = {
    val node1: A = graph.vertices.toList(indexNode)
    val neighborHood = graph.adjacenceMatrix.getOrElse(node1, Set())
    val nodes = neighborHood.map((node, weight) => node).toList
    val node2 = nodes(indexNeighborhood)
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
    val g = GraphUnDirected(Map("A" -> Set(("C", 2), ("D", 5)), "B" -> Set(("C", 1)), "C" -> Set(("D", 1)), "D" -> Set()))
    val g2 = symmetricMatrix(g)
    print(g2.hasCycle())
  }
}