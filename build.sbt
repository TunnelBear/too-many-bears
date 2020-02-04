name := "ratelimiter2"

organization := "org.tunnelbear"

version := "1.0"

scalaVersion := "2.12.6"

libraryDependencies += "org.specs2" %% "specs2-core" % "4.8.3" % "test"
libraryDependencies += "org.specs2" %% "specs2-mock" % "4.8.3" % "test"

// Cross-build to support Scala 2.11 projects (remembackend...), and Scala 2.12 projects (tbearDashboard2, polarbackend...)
// See https://www.scala-sbt.org/1.x/docs/Cross-Build.html
lazy val root = (project in file(".")).settings(
  crossScalaVersions := List("2.12.6", "2.11.7"))
