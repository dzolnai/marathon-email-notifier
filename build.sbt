name := "marathon-email-notifier"

version := "0.1"

scalaVersion := "2.10.6"

// Dependencies required for this project
libraryDependencies ++= Seq(
  "org.apache.zookeeper" % "zookeeper" % "3.4.8",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "org.slf4j" % "slf4j-log4j12" % "1.7.21",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)