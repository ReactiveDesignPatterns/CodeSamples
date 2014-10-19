scalaVersion := "2.10.3"

name := "RDP-Ch2-Actor"

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.3.0",
          "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
          "ch.qos.logback" % "logback-classic" % "1.0.10",
          "com.typesafe.akka" %% "akka-testkit" % "2.3.0" % "test",
          "org.scalatest" %% "scalatest" % "2.0" % "test"
)


