import sbt.Keys.version

val `akka-persistence-chaos-journal` = (project in file("."))
  .settings(Settings.scalaVersions)
  .settings(Settings.publish)
  .settings(
    name := "akka-persistence-chaos-journal",
    organization := "org.psliwa",
    startYear := Some(2017),
    version := Versions.pluginFullVersion,

    libraryDependencies := Seq(
      "com.github.dnvriend" %% "akka-persistence-inmemory" % s"${Versions.akkaVersion}.1",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "3.0.4" % "test",
      "com.typesafe.akka" %% "akka-testkit" % Versions.akkaVersion % "test"
    )
  )
