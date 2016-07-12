name := "marathon-email-notifier"

version := "0.1"

scalaVersion := "2.10.6"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

// Dependencies required for this project
libraryDependencies ++= Seq(
  "org.apache.zookeeper" % "zookeeper" % "3.4.8",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "org.slf4j" % "slf4j-log4j12" % "1.7.21",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "com.typesafe" % "config" % "1.3.0"
)