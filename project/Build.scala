import sbt._
import Keys._

import Resolvers._
import Dependencies._
import BuildSettings._

object BuildSettings {
  val buildOrganization = "com.github.scadulix"
  val buildVersion      = "0.0.1"
  val buildScalaVersion = "2.9.1"
  val akkaVersion       = "1.2"
  val latest            = "latest.integration"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object ScadulixBuild extends Build {

  // -----------------------------------------------------------------------
  // project definitions
  // -----------------------------------------------------------------------

  lazy val scadulix = Project ( "scadulix", file ("."),
    settings = buildSettings ++ Seq (
      libraryDependencies ++= Seq ( actor, conf, specs2 )
    )
  )
}

object Dependencies {

  // -----------------------------------------------------------------------
  // compile
  // -----------------------------------------------------------------------

  val actor = "se.scalablesolutions.akka" %  "akka-actor" % akkaVersion
  val conf  = "com.github.scf4s"          %% "scf4s"      % latest

  // -----------------------------------------------------------------------
  // test
  // -----------------------------------------------------------------------

  val specs2 = "org.specs2" %% "specs2" % "1.7.1" % "test"

}

object Resolvers {
  val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
}
