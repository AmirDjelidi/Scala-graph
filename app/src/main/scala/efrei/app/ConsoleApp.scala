package efrei.app

import zio._
import zio.Console._
import efrei.app.model.{Batiment, Salle}
import efrei.app.service.GraphService
import graph.{Graph, GraphDirected, GraphUnDirected, GraphVizExport} // Importer les types concrets
import graph.GraphVizExport._

object ConsoleApp {

  // Helper pour filtrer le graphe. Utilise le pattern matching pour accéder à la matrice.
  private def createBuildingSubgraph(fullGraph: Graph[Salle], buildingName: String): Graph[Salle] = {
    fullGraph match {
      case g: GraphDirected[Salle] =>
        val buildingVertices = g.vertices.filter(_.batiment.nom.equalsIgnoreCase(buildingName))
        val newAdjMatrix = g.adjacenceMatrix
          .filter { case (key, _) => buildingVertices.contains(key) }
          .view.mapValues { neighbors =>
            neighbors.filter { case (neighborNode, _) => buildingVertices.contains(neighborNode) }
          }.toMap
        GraphDirected(newAdjMatrix)
      case _ => fullGraph // Si ce n'est pas un graphe dirigé, on ne fait rien pour l'instant
    }
  }

  // Helper pour l'affichage. Utilise le pattern matching pour accéder à la matrice.
  private def formatPathWithDetails[A](path: List[A], graph: Graph[A]): String = {
    if (path.length < 2) return path.mkString

    // On extrait la matrice d'adjacence en toute sécurité grâce au pattern matching
    val adjMatrix: Map[A, Set[(A, Int)]] = graph match {
      case g: GraphDirected[A] => g.adjacenceMatrix
      case g: GraphUnDirected[A] => g.adjacenceMatrix
      case _ => Map.empty
    }

    path.head.toString + path.sliding(2).map { case List(u, v) =>
      val weight = adjMatrix.get(u).flatMap(_.find(_._1 == v).map(_._2)).getOrElse(0)
      (u, v) match {
        case (s1: Salle, s2: Salle) if s1.batiment != s2.batiment =>
          s" ->($weight)-> \n\t--- Changement de Bâtiment (${s1.batiment.nom} -> ${s2.batiment.nom}) ---\n\t${s2.nom}"
        case _ => s" ->($weight)-> ${v.toString}"
      }
    }.mkString
  }

  val appLogic: ZIO[GraphService, Throwable, Unit] = {
    val menu =
      """
        |--- Menu Principal ---
        |1. Lister tous les bâtiments (GraphViz)
        |2. Trouver un chemin entre deux bâtiments
        |3. Trouver un chemin entre deux salles
        |4. Afficher le graphe d'un bâtiment spécifique
        |q. Quitter
        |""".stripMargin

    def loop: ZIO[GraphService, Throwable, Unit] =
      for {
        _        <- printLine(menu)
        command  <- readLine("> ")
        graphSvc <- ZIO.service[GraphService]
        continue <- command match {
          case "1" =>
            graphSvc.getBatimentGraph.flatMap(g => printLine(g.toGraphViz())).as(true)
          case "2" =>
            for {
              batimentGraph <- graphSvc.getBatimentGraph
              _             <- printLine("Bâtiments disponibles: " + batimentGraph.vertices.mkString(", "))
              start         <- readLine("Bâtiment de départ : ")
              end           <- readLine("Bâtiment d'arrivée : ")
              effect        =  ZIO.attempt(batimentGraph.dijkstra(start, end))
              _             <- effect.foldZIO(
                e => printLineError(s"Erreur: Impossible de trouver un chemin. (${e.getMessage})"),
                { case (path, cost) =>
                  for {
                    _ <- printLine(s"✅ Chemin trouvé (Coût total: $cost)")
                    _ <- printLine(formatPathWithDetails(path, batimentGraph))
                    _ <- printLine("--- Graphe complet avec chemin surligné (Graphviz) ---")
                    _ <- printLine(batimentGraph.toGraphViz(highlightPath = path))
                    _ <- printLine("----------------------------------------------------")
                  } yield ()
                }
              )
            } yield true
          case "3" =>
            val findSallePath = for {
              sallesGraph <- graphSvc.getSallesGraph
              _ <- printLine("--- Salle de départ ---")
              startNomSalle <- readLine("Nom de la salle : ")
              startNomBatiment <- readLine("Bâtiment (A, B, K, H) : ")
              _ <- printLine("--- Salle d'arrivée ---")
              endNomSalle <- readLine("Nom de la salle : ")
              endNomBatiment <- readLine("Bâtiment (A, B, K, H) : ")
              startBatiment <- ZIO.fromOption(Batiment.fromString(startNomBatiment)).mapError(_ => new Error(s"Bâtiment de départ '$startNomBatiment' invalide."))
              endBatiment <- ZIO.fromOption(Batiment.fromString(endNomBatiment)).mapError(_ => new Error(s"Bâtiment d'arrivée '$endNomBatiment' invalide."))
              startSalle = Salle(startNomSalle, startBatiment)
              endSalle = Salle(endNomSalle, endBatiment)
              pathAndCost <- ZIO.attempt(sallesGraph.dijkstra(startSalle, endSalle)).mapError(e => new Error(s"Impossible de calculer le chemin. Vérifiez que les salles existent. Détail: ${e.getMessage}"))
              (path, cost) = pathAndCost
              _ <- printLine(s"✅ Chemin trouvé (Coût total: $cost)")
              _ <- printLine(formatPathWithDetails(path, sallesGraph))
              _ <- printLine("--- Graphe complet avec chemin surligné (Graphviz) ---")
              _ <- printLine(sallesGraph.toGraphViz(highlightPath = path))
              _ <- printLine("----------------------------------------------------")
            } yield ()
            findSallePath.catchAll(e => printLineError(s"Erreur : ${e.getMessage}")).as(true)
          case "4" =>
            for {
              nomBatiment <- readLine("Nom du bâtiment à afficher (A, B, K, H) : ")
              fullSallesGraph <- graphSvc.getSallesGraph
              subgraph = createBuildingSubgraph(fullSallesGraph, nomBatiment)
              _ <- printLine(s"--- Graphe du bâtiment $nomBatiment (Graphviz) ---")
              _ <- printLine(subgraph.toGraphViz())
              _ <- printLine("--------------------------------------------")
            } yield true
          case "q" =>
            printLine("Au revoir !").as(false)
          case _ =>
            printLineError("Commande invalide.").as(true)
        }
        _ <- if (continue) loop else ZIO.unit
      } yield ()
    loop
  }
}