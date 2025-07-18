// dans core/src/main/scala/graph/GraphVizExport.scala
package graph

object GraphVizExport {

  /**
   * Ajoute la méthode .toGraphViz à tous les objets de type Graph[A]
   * en utilisant une extension de méthode (Scala 3).
   */
  extension [A](graph: Graph[A]) {
    def toGraphViz: String = {
      // 1. Déterminer si le graphe est orienté ou non pour la syntaxe DOT
      val (graphType, edgeConnector) = graph match {
        case _: GraphDirected[_]   => ("digraph", "->")
        case _: GraphUnDirected[_] => ("graph", "--")
        case _                     => ("graph", "--") // Par défaut
      }

      val header = s"$graphType G {\n"
      val footer = "}\n"

      // 2. Utiliser foldLeft pour construire la chaîne de toutes les arêtes
      val edgesStr = graph.edges.foldLeft("") { (accumulator, edge) =>
        val edgeLine = edge match {
          case EdgeDirected(from, to, weight) =>
            s"""  "$from" $edgeConnector "$to" [label="$weight"];\n"""
          case EdgeUndirected(node1, node2, weight) =>
            // Pour les graphes non-orientés, on évite les doublons dans la visualisation
            if (accumulator.contains(s"""  "$node2" $edgeConnector "$node1" """)) ""
            else s"""  "$node1" $edgeConnector "$node2" [label="$weight"];\n"""
        }
        accumulator + edgeLine
      }

      header + edgesStr + footer
    }
  }
}