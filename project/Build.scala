import sbt._

object Build {
  val akkaVersion = "2.4-M2"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion
  val akkaDData = "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion

  val amazonAWS = "com.amazonaws" % "aws-java-sdk" % "1.10.14"

  val sbtIO = "org.scala-sbt" %% "io" % "0.13.8"

  val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.2"
  val scalaJava8 = "org.scala-lang.modules" %% "scala-java8-compat" % "0.6.0" exclude("org.scala-lang.modules", "fngen_2.11")

  val playJson = "com.typesafe.play" %% "play-json" % "2.4.0"

  val scalatest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  val junit = "junit" % "junit" % "4.11" % "test"
}
