name := "marathon-email-notifier"

version := "0.1"

scalaVersion := "2.10.6"

// Dependencies required for this project
libraryDependencies ++= Seq(
  "org.apache.zookeeper" % "zookeeper" % "3.4.8",
  "com.github.scopt" %% "scopt" % "3.5.0"
)