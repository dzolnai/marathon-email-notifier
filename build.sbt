name := "marathon-email-notifier"

version := "0.1"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

// Dependencies required for this project
libraryDependencies ++= Seq(
  // Zookeeper client and server (for the unit tests)
  "org.apache.zookeeper" % "zookeeper" % "3.4.8",
  // Logging
  "org.slf4j" % "slf4j-log4j12" % "1.7.21",
  // Configuration parser
  "com.typesafe" % "config" % "1.3.0",
  // Dependency injection
  "com.softwaremill.macwire" %% "macros" % "2.2.3" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.3",
  "com.softwaremill.macwire" %% "proxy" % "2.2.3",
  // HTTP client
  "org.http4s" %% "http4s-dsl" % "0.14.1",
  "org.http4s" %% "http4s-blaze-client" % "0.14.1",
  // JSON (de)serialization
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  // SMTP client
  "org.apache.commons" % "commons-email" % "1.4",
  // HTTP server for unit tests
  "org.http4s" %% "http4s-blaze-server" % "0.14.1" % "test",
  // Unit test runner
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  // SMTP server for unit tests
  "com.github.kirviq" % "dumbster" % "1.7.1" % "test"
)