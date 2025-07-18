/* =================================================================================
 * Configuration Globale pour tout le projet
 * ================================================================================= */

ThisBuild / version := "0.1.0-SNAPSHOT"

// On utilise Scala 3. La version la plus récente et stable est un bon choix.
ThisBuild / scalaVersion := "3.3.3"


/* =================================================================================
 * Définition du projet racine et des sous-projets (modules)
 * ================================================================================= */

// Projet racine qui ne contient pas de code mais qui "agrège" les autres.
// Lancer `sbt compile` à la racine compilera `core` puis `app`.
lazy val root = (project in file("."))
  .aggregate(core, app)
  .settings(
    name := "functional-graphs"
  )

// --- Module 1: La bibliothèque `core` ---
// Contient la logique des graphes, les algorithmes et les utilitaires.
lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      // Librairie pour la conversion en JSON
      "dev.zio" %% "zio-json" % "0.6.2",

      // Librairie pour les tests unitaires
      "org.scalatest" %% "scalatest" % "3.2.18" % Test
    )
  )

// --- Module 2: L'application console `app` ---
// Contient l'application interactive en ZIO qui utilise le module `core`.
lazy val app = (project in file("app"))
  .dependsOn(core) // <-- Très important: `app` dépend de `core`
  .settings(
    name := "app",
    libraryDependencies ++= Seq(

      "dev.zio" %% "zio" % "2.0.22",

      "dev.zio" %% "zio-json" % "0.6.2"
    )
  )

