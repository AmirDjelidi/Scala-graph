// dans app/src/main/scala/Model.scala
package efrei.app

// Utiliser un `enum` est plus propre pour des valeurs fixes comme les noms de campus
enum Campus(val nom: String) {
  case Gorki extends Campus("Gorki")
  case Republique extends Campus("République")
  case Home extends Campus("Home")
}

// Le sommet de votre graphe orienté sera une salle, identifiée par son nom et son campus
case class Salle(nom: String, campus: Campus)