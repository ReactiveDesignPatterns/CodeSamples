resolvers += "twitter" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  Dependencies.akka25Contrib,
  Dependencies.akka25DData,
  Dependencies.playJson,
  Dependencies.fasterxml,
  Dependencies.sbtIO,
  Dependencies.akka25Testkit,
  Dependencies.finagle,
  Dependencies.junit,
  Dependencies.scalatest) ++ Dependencies.ckites
