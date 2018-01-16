import sbt.Keys.version

val AkkaVersion = "2.5.1"
val PluginVersion = "1-SNAPSHOT"
val ScalaVersion = "2.12.4"

val akkaPersistenceChaosJournal = (project in file("."))
  .settings(
    name := "akka-persistence-chaos-journal",
    organization := "org.psliwa",
    startYear := Some(2017),
    version := s"$AkkaVersion.$PluginVersion",

    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq("2.11.12", ScalaVersion),

    libraryDependencies := Seq(
      "com.github.dnvriend" %% "akka-persistence-inmemory" % s"$AkkaVersion.1",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "3.0.4" % "test",
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test"
    ),

    publishArtifact in Test := false,
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := <xml:group>
      <developers>
        <developer>
          <id>psliwa</id>
          <name>Piotr Śliwa</name>
          <url>https://github.com/psliwa</url>
        </developer>
      </developers>
    </xml:group>
  )
