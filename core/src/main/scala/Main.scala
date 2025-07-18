import graph.GraphDirected
import graph.JsonCodecs.given
import zio.json.*

object Main extends App {

  val reseauVilles = GraphDirected(
    Map(
      "Paris" -> Set(
        ("Lille", 225),
        ("Lyon", 465),
        ("Bordeaux", 585),
        ("Strasbourg", 490)
      ),
      "Lille" -> Set(
        ("Paris", 225)
      ),
      "Lyon" -> Set(
        ("Marseille", 315),
        ("Genève", 150)
      ),
      "Bordeaux" -> Set(
        ("Paris", 585),
        ("Toulouse", 245)
      ),
      "Strasbourg" -> Set.empty[(String, Int)], // Impasse
      "Marseille" -> Set(
        ("Lyon", 315)
      ),
      "Genève" -> Set.empty[(String, Int)], // Impasse
      "Toulouse" -> Set.empty[(String, Int)]  // Impasse
    )
  )

  println("--- Graphe du réseau de villes ---")
  println(reseauVilles)

  println("\n--- Conversion du réseau en JSON ---")
  val jsonString: String = reseauVilles.toJson
  println(jsonString)

  println("\n--- Conversion du JSON vers le graphe ---")
  val decodedGraph = jsonString.fromJson[GraphDirected[String]]

  decodedGraph match {
    case Right(g_decoded) =>
      println("✅ Succès du décodage !")
      println(s"Les graphes sont-ils identiques ? ${g_decoded == reseauVilles}")

    case Left(error) =>
      println(s"❌ Erreur de décodage : $error")
  }
}