package graph

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// Imports ajoutés pour les tests de l'étape 2
import zio.json.*
import graph.JsonCodecs.given
import graph.GraphVizExport.*

class GraphSpec extends AnyFlatSpec with Matchers {

  behavior of "GraphDirected"

  it should "add and remove edges correctly" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 1))
      .addEdge(EdgeDirected("B", "C", 2))

    g.edges should contain theSameElementsAs List(
      EdgeDirected("A", "B", 1),
      EdgeDirected("B", "C", 2)
    )

    val g2 = g.removeEdge(EdgeDirected("A", "B", 1))
    g2.edges should not contain EdgeDirected("A", "B", 1)
  }

  it should "perform DFS correctly" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 1))
      .addEdge(EdgeDirected("B", "C", 1))
      .addEdge(EdgeDirected("C", "A", 1))

    g.DFS() shouldBe true
  }

  it should "perform BFS correctly" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 1))
      .addEdge(EdgeDirected("A", "C", 1))
      .addEdge(EdgeDirected("B", "D", 1))

    g.BFS("A") should contain allOf ("A", "B", "C", "D")
  }

  it should "detect cycles correctly in directed graph" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 1))
      .addEdge(EdgeDirected("B", "C", 1))
      .addEdge(EdgeDirected("C", "A", 1))

    g.hasCycle() shouldBe true

    val g2 = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 1))
      .addEdge(EdgeDirected("B", "C", 1))

    g2.hasCycle() shouldBe false
  }

  it should "find shortest path with Dijkstra (directed)" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 1))
      .addEdge(EdgeDirected("B", "C", 2))
      .addEdge(EdgeDirected("A", "C", 5))

    val (path, cost) = g.dijkstra("A", "C")
    path shouldBe List("A", "B", "C")
    cost shouldBe 3
  }

  behavior of "GraphUnDirected"

  it should "add and remove edges symmetrically" in {
    val g = GraphUnDirected[String](Map())
      .addEdge(EdgeUndirected("A", "B", 2))
      .addEdge(EdgeUndirected("B", "C", 3))

    // Note: l'ordre n'est pas garanti, on vérifie juste la présence
    g.edges should contain allOf (
      EdgeUndirected("A", "B", 2),
      EdgeUndirected("B", "A", 2),
      EdgeUndirected("B", "C", 3),
      EdgeUndirected("C", "B", 3)
    )

    val g2 = g.removeEdge(EdgeUndirected("A", "B", 2))
    g2.edges should not contain EdgeUndirected("A", "B", 2)
  }

  it should "perform DFS correctly in undirected graph" in {
    val g = GraphUnDirected[String](Map())
      .addEdge(EdgeUndirected("A", "B", 1))
      .addEdge(EdgeUndirected("B", "C", 1))

    g.DFS() shouldBe true
  }

  it should "perform BFS correctly in undirected graph" in {
    val g = GraphUnDirected[String](Map())
      .addEdge(EdgeUndirected("A", "B", 1))
      .addEdge(EdgeUndirected("B", "C", 1))

    g.BFS("A") should contain allOf ("A", "B", "C")
  }

  it should "detect cycles correctly in undirected graph" in {
    val g = GraphUnDirected[String](Map())
      .addEdge(EdgeUndirected("A", "B", 1))
      .addEdge(EdgeUndirected("B", "C", 1))
      .addEdge(EdgeUndirected("C", "A", 1))

    g.hasCycle() shouldBe true

    val g2 = GraphUnDirected[String](Map())
      .addEdge(EdgeUndirected("A", "B", 1))
      .addEdge(EdgeUndirected("B", "C", 1))

    g2.hasCycle() shouldBe false
  }

  it should "find shortest path with Dijkstra (undirected)" in {
    val g = GraphUnDirected[String](Map())
      .addEdge(EdgeUndirected("A", "B", 1))
      .addEdge(EdgeUndirected("B", "C", 2))
      .addEdge(EdgeUndirected("A", "C", 5))

    val (path, cost) = g.dijkstra("A", "C")
    path shouldBe List("A", "B", "C")
    cost shouldBe 3
  }

  // Comportement hérité de l'ancien code, laissé pour la complétude
  behavior of "GraphUnDirected.symmetricMatrix"

  it should "symmetrize adjacency matrix correctly" in {
    val g = GraphUnDirected(Map(
      "A" -> Set(("B", 1)),
      "B" -> Set()
    ))

    val g2 = GraphUnDirected.symmetricMatrix(g)
    g2.adjacenceMatrix("B") should contain(("A", 1))
  }

  // --- NOUVEAUX TESTS POUR L'ÉTAPE 2 ---

  behavior of "Graph JSON serialization"

  it should "serialize and deserialize a directed graph correctly" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("Paris", "Lyon", 465))

    val jsonString = g.toJson
    val decodedGraph = jsonString.fromJson[GraphDirected[String]]

    decodedGraph shouldBe Right(g)
  }

  it should "serialize and deserialize an undirected graph correctly" in {
    val g = GraphUnDirected[String](Map.empty)
      .addEdge(EdgeUndirected("Gorki", "République", 15))

    val jsonString = g.toJson
    val decodedGraph = jsonString.fromJson[GraphUnDirected[String]]

    decodedGraph shouldBe Right(g)
  }

  behavior of "GraphViz export"

  it should "generate correct GraphViz DOT string for a directed graph" in {
    val g = GraphDirected[String](Map.empty)
      .addEdge(EdgeDirected("A", "B", 5))

    val dotString = g.toGraphViz
    dotString should startWith("digraph G")
    dotString should include(""""A" -> "B" [label="5"];""")
    dotString should endWith("}\n")
  }

  it should "generate correct GraphViz DOT string for an undirected graph" in {
    val g = GraphUnDirected[String](Map.empty)
      .addEdge(EdgeUndirected("A", "B", 10))

    val dotString = g.toGraphViz
    dotString should startWith("graph G")
    dotString should include(""""A" -- "B" [label="10"];""")
    dotString should endWith("}\n")
  }
}