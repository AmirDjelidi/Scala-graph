package graph

import graph.{Edge, EdgeDirected, EdgeUndirected}

object GraphVizExport {

  private def nodeToId[A](node: A): String = s""""${node.toString.replaceAll("\"", "\\\"")}""""

  implicit class GraphVizOps[A](graph: Graph[A]) {

    def toGraphViz(highlightPath: List[A] = Nil): String = {
      val pathEdges = highlightPath.sliding(2).collect { case List(a, b) => (a, b) }.toSet
      val startNodeOpt = highlightPath.headOption
      val endNodeOpt = highlightPath.lastOption

      val graphType = if (graph.isInstanceOf[GraphUnDirected[A]]) "graph" else "digraph"
      val separator = if (graph.isInstanceOf[GraphUnDirected[A]]) "--" else "->"

      // --- CORRECTION FINALE ---
      // On déduit tous les noeuds à partir de la liste des arêtes,
      // ce qui est plus robuste que de se fier à .vertices
      val allNodesInGraph = graph.edges.flatMap {
        case EdgeDirected(from, to, _) => List(from, to)
        case EdgeUndirected(node1, node2, _) => List(node1, node2)
      }.toSet ++ graph.vertices // On ajoute les `vertices` pour les nœuds isolés

      val verticesStr = allNodesInGraph.map { v =>
        val id = nodeToId(v)
        val style = ", style=filled, fontcolor=white"

        val attributes =
          if (startNodeOpt.contains(v)) s"""[color="dodgerblue"$style]"""
          else if (endNodeOpt.contains(v)) s"""[color="seagreen"$style]"""
          else if (highlightPath.contains(v)) s"""[color="crimson"$style]"""
          else ""

        s"  $id $attributes;"
      }.mkString("\n")

      // Le reste du code reste identique
      val edgesStr = {
        if (graphType == "graph") {
          val initialState = (Set.empty[Set[A]], List.empty[String])
          val (_, dotStrings) = graph.edges.foldLeft(initialState) {
            case ((processed, acc), edge) =>
              val (u, v, weight) = edge match {
                case EdgeUndirected(n1, n2, w) => (n1, n2, w)
                case EdgeDirected(f, t, w)   => (f, t, w)
              }
              val edgeSet = Set(u, v)
              if (processed.contains(edgeSet)) {
                (processed, acc)
              } else {
                val (fromId, toId) = (nodeToId(u), nodeToId(v))
                val isHighlighted = pathEdges.contains((u, v)) || pathEdges.contains((v, u))
                val attributes = if (isHighlighted) s"""[label="$weight", color=crimson, penwidth=2.0]""" else s"""[label="$weight"]"""
                val newDotString = s"  $fromId $separator $toId $attributes;"
                (processed + edgeSet, newDotString :: acc)
              }
          }
          dotStrings.reverse.mkString("\n")
        } else {
          graph.edges.map { edge =>
            val (from, to, weight) = edge match {
              case EdgeDirected(f, t, w)   => (f, t, w)
              case EdgeUndirected(n1, n2, w) => (n1, n2, w)
            }
            val (fromId, toId) = (nodeToId(from), nodeToId(to))
            val attributes =
              if (pathEdges.contains((from, to))) s"""[label="$weight", color=crimson, penwidth=2.0]"""
              else s"""[label="$weight"]"""
            s"""  $fromId $separator $toId $attributes;"""
          }.mkString("\n")
        }
      }

      s"""
         |$graphType G {
         |  rankdir=LR;
         |  node [shape=box, style=rounded];
         |
         |$verticesStr
         |
         |$edgesStr
         |}
       """.stripMargin
    }
  }
}