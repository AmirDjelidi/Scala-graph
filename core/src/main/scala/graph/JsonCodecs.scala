package graph

import zio.json.*

object JsonCodecs {

  // On explique à zio-json comment gérer une Map[K, V] en la transformant
  // en une List[(K, V)], car il sait encoder une liste de paires.
  implicit def mapEncoder[K: JsonEncoder, V: JsonEncoder]: JsonEncoder[Map[K, V]] =
    JsonEncoder.list[(K, V)].contramap(_.toList)

  // On explique comment faire l'inverse pour le décodage.
  implicit def mapDecoder[K: JsonDecoder, V: JsonDecoder]: JsonDecoder[Map[K, V]] =
    JsonDecoder.list[(K, V)].map(_.toMap)

  
  given [A: JsonEncoder : JsonDecoder]: JsonCodec[GraphDirected[A]] = JsonCodec.derived
  given [A: JsonEncoder : JsonDecoder]: JsonCodec[GraphUnDirected[A]] = JsonCodec.derived
}