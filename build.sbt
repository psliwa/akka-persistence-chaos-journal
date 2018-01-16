
val `akka-persistence-chaos-journal` = project in file("akka-persistence-chaos-journal")
val example = project in file("example")

val root = (project in file("."))
  .settings(Settings.scalaVersions)
  .aggregate(`akka-persistence-chaos-journal`, example)
