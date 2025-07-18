package efrei.app.service

import zio._
import zio.json._
import graph._
import efrei.app.model._
import scala.io.Source

// --- NOTE ---
// Assurez-vous d'importer les types d'arêtes depuis votre bibliothèque `core`
import graph.{Edge, EdgeDirected, EdgeUndirected}


trait GraphService {
  def getBatimentGraph: UIO[GraphUnDirected[String]]
  def getSallesGraph: UIO[GraphDirected[Salle]]
}

case class GraphServiceLive(
                             batimentGraph: GraphUnDirected[String],
                             sallesGraph: GraphDirected[Salle]
                           ) extends GraphService {
  def getBatimentGraph: UIO[GraphUnDirected[String]] = ZIO.succeed(batimentGraph)
  def getSallesGraph: UIO[GraphDirected[Salle]] = ZIO.succeed(sallesGraph)
}

object GraphService {
  private def loadGraph[G <: Graph[?] : JsonDecoder](fileName: String): ZIO[Scope, Throwable, G] =
    for {
      source <- ZIO.fromAutoCloseable(ZIO.attempt(Source.fromResource(fileName)))
      jsonStr <- ZIO.attempt(source.mkString)
      graph <- ZIO.fromEither(jsonStr.fromJson[G])
        .mapError(msg => new Error(s"Erreur de parsing JSON dans '$fileName': $msg"))
    } yield graph

  val live: ZLayer[Any, Throwable, GraphService] =
    ZLayer.scoped {
      import graph.JsonCodecs.given
      import efrei.app.model.Batiment.codec
      import efrei.app.model.Salle.codec

      for {
        _ <- ZIO.logInfo("Chargement du graphe des bâtiments...")
        batimentGraph <- loadGraph[GraphUnDirected[String]]("batiments.json")
        _ <- ZIO.logInfo("Chargement du graphe des salles...")
        initialSallesGraph <- loadGraph[GraphDirected[Salle]]("salles.json")
        _ <- ZIO.logInfo("Fusion des graphes en connectant les 'Accueil'...")

        // --- FUSION DES GRAPHES (VERSION CORRIGÉE) ---
        finalSallesGraph = batimentGraph.edges.foldLeft(initialSallesGraph) { (graphe, arete) =>
          arete match {
            case EdgeUndirected(b1Str: String, b2Str: String, poids: Int) =>
              val sallesHub = for {
                b1 <- Batiment.fromString(b1Str)
                b2 <- Batiment.fromString(b2Str)
              } yield (Salle("Accueil", b1), Salle("Accueil", b2))

              sallesHub match {
                case Some((accueil1, accueil2)) =>
                  graphe
                    .addEdge(EdgeDirected(accueil1, accueil2, poids))
                    .addEdge(EdgeDirected(accueil2, accueil1, poids))
                case None =>
                  graphe
              }
            case _ => graphe // Ignore les autres types d'arêtes au cas où
          }
        }
        _ <- ZIO.logInfo("✅ Graphes fusionnés avec succès.")
      } yield GraphServiceLive(batimentGraph, finalSallesGraph)
    }
}