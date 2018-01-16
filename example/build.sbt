import sbt.Keys.version

// It is an alias for running tests with ChaosJournal as persistent journal.
// Tests with in-memory journal may be run using standard "example/test" command
addCommandAlias("chaosTest", "; project example ; set javaOptions in Test += \"-Dconfig.resource=chaos-application.conf\" ; test")

val example = (project in file("."))
  .settings(Settings.scalaVersions)
  .settings(
    name := "akka-persistence-chaos-journal-example",
    organization := "org.psliwa",
    startYear := Some(2017),
    version := Versions.pluginFullVersion,

//    For testing staging releases
//    resolvers += "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/repositories/orgpsliwa-1000/content",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",

    fork in Test := true,

    publish := {},

    libraryDependencies := Seq(
      "org.psliwa" %% "akka-persistence-chaos-journal" % Versions.pluginFullVersion,
      "com.typesafe.akka" %% "akka-persistence" % Versions.akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % Versions.akkaVersion,
      "org.scalatest" %% "scalatest" % "3.0.4" % "test"
    )
  )
