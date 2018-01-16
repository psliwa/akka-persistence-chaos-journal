import sbt._
import sbt.Keys._

object Settings {
  val scalaVersions = Seq(
    scalaVersion := Versions.scalaVersion,
    crossScalaVersions := Seq("2.11.12", Versions.scalaVersion)
  )

  val publish = Seq(
    publishArtifact in Test := false,
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    scmInfo := Some(ScmInfo(
      browseUrl = url("https://github.com/psliwa/akka-persistence-chaos-journal"),
      connection = "scm:git:git@github.com:psliwa/akka-persistence-chaos-journal.git"
    )),
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
}
