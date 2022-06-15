import sbt.Keys.libraryDependencies

lazy val root = project
  .in(file("."))
  .settings(
    name := "archunit-scala",
    description := "Examples of Archunit for Scala",
    version := "0.1.0",
    scalaVersion := "3.0.1",
    libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.5", "org.slf4j" % "slf4j-simple" % "1.7.5"),
    libraryDependencies += "com.tngtech.archunit" % "archunit" % "0.20.0" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.9" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
  )


