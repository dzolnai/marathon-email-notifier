name := "marathon-email-notifier"

version := "0.1"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

// Dependencies required for this project
libraryDependencies ++= Seq(
  "org.apache.zookeeper" % "zookeeper" % "3.4.8",
  "org.slf4j" % "slf4j-log4j12" % "1.7.21",
  "com.typesafe" % "config" % "1.3.0",
  "com.softwaremill.macwire" %% "macros" % "2.2.3" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.3",
  "com.softwaremill.macwire" %% "proxy" % "2.2.3",
  "org.http4s" %% "http4s-dsl" % "0.14.1",
  "org.http4s" %% "http4s-blaze-client" % "0.14.1",
  "org.json4s" %% "json4s-native" % "3.4.0",
  "org.json4s" %% "json4s-ext" % "3.4.0",
  "org.http4s" %% "http4s-blaze-server" % "0.14.1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)