import sbt._
import Keys._

import Resolvers._
import Dependencies._
import BuildSettings._

object BuildSettings {
  val buildOrganization = "scadulix"
  val buildVersion      = "0.0.1"
  val buildScalaVersion = "2.9.1"
  val akkaVersion       = "1.1.3"

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

  lazy val scadulix = Project (
    "scadulix",
    file ("."),
    settings = buildSettings ++ Seq (
      libraryDependencies ++= Seq ( akkaActor, specs2 )
    )
  ) dependsOn ( scalaxconf )
}

object Dependencies {

  // -----------------------------------------------------------------------
  // compile
  // -----------------------------------------------------------------------

  val akkaActor = "se.scalablesolutions.akka" % "akka-actor" % akkaVersion
  val scalaxconf = uri ( "git://github.com/wookietreiber/scalaxconf.git" )

  // -----------------------------------------------------------------------
  // test
  // -----------------------------------------------------------------------

  val specs2 = "org.specs2" %% "specs2" % "1.6.1" % "test"
}

object Resolvers {
  val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
}
