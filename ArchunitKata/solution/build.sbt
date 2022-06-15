import sbt.Keys.libraryDependencies

lazy val root = project
  .in(file("."))
  .settings(
    name := "archunit-scala",
    description := "Examples of Archunit for Scala",
    version := "0.1.0",
    scalaVersion := "2.11.12",
    libraryDependencies += "com.tngtech.archunit" % "archunit" % "0.23.1" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.9" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
  )


