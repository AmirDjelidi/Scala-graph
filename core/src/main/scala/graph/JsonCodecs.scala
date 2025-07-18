package graph

import zio.json.*
import zio.json.ast.Json
import scala.util.Try

object JsonCodecs {

  // --- Codecs pour les Maps avec des clés non-String ---
  // On explique comment transformer une Map[K, V] en une List[(K, V)] et vice-versa.
  // zio-json sait comment gérer une List[(K, V)].

  implicit def mapEncoder[K: JsonEncoder, V: JsonEncoder]: JsonEncoder[Map[K, V]] =
    JsonEncoder.list[(K, V)].contramap(_.toList)

  implicit def mapDecoder[K: JsonDecoder, V: JsonDecoder]: JsonDecoder[Map[K, V]] =
    JsonDecoder.list[(K, V)].map(_.toMap)


  // --- Tes codecs de Graphe (qui vont maintenant fonctionner) ---

  // On utilise "JsonCodec.derived" qui est une syntaxe plus simple et moderne en Scala 3.
  // Ça fait la même chose que DeriveJsonCodec.gen.
  given [A: JsonEncoder : JsonDecoder]: JsonCodec[GraphDirected[A]] = JsonCodec.derived
  given [A: JsonEncoder : JsonDecoder]: JsonCodec[GraphUnDirected[A]] = JsonCodec.derived
}