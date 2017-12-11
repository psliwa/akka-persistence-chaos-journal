import sbt.Keys.version

val AkkaVersion = "2.5.1"

val akkaPersistenceChaosJournal = (project in file("."))
  .settings(
    name := "akka-persistence-chaos-journal",
    organization := "org.psliwa",
    version := s"$AkkaVersion.0-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies := Seq(
      "com.github.dnvriend" %% "akka-persistence-inmemory" % s"$AkkaVersion.1",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "3.0.4" % "test",
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test"
    ),
    crossScalaVersions := Seq("2.11.12", "2.12.4"),
    publishArtifact in Test := false,
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
        </developer>
      </developers>
    </xml:group>
  )
