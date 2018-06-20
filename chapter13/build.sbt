import Build._

resolvers += "twitter" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  akka25Contrib,
  akka25DData,
  playJson,
  fasterxml,
  sbtIO,
  akka25Testkit,
  finagle,
  junit,
  scalatest) ++ ckites
