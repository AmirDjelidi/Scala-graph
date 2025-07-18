package efrei.app.model

import zio.json._

// 1. Renommage de Building en Batiment pour être cohérent
enum Batiment(val nom: String) {
  case A extends Batiment("A")
  case B extends Batiment("B")
  case C extends Batiment("C")
  case G extends Batiment("G")
  case H extends Batiment("H")
  case K extends Batiment("K")
}

object Batiment {
  def fromString(s: String): Option[Batiment] =
    values.find(_.nom.equalsIgnoreCase(s))

  implicit val codec: JsonCodec[Batiment] = JsonCodec.string.transformOrFail(
    s => Batiment.fromString(s).toRight(s"Le Batiment '$s' est invalide."),
    _.nom
  )
}

// Case class pour représenter une salle
// 2. Le champ 'batiment' commence par une minuscule et a le bon type 'Batiment'
case class Salle(nom: String, batiment: Batiment)

object Salle {
  implicit val codec: JsonCodec[Salle] = DeriveJsonCodec.gen[Salle]
}