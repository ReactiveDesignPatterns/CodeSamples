import sbt._

object Build {
  val akkaVersion = "2.3.11"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion

  val sbtIO = "org.scala-sbt" %% "io" % "0.13.8"

  val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.2"

  val playJson = "com.typesafe.play" %% "play-json" % "2.4.0"

  val scalatest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  val junit = "junit" % "junit" % "4.11" % "test"
}
