import Build._

resolvers += "twitter" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  akkaContrib,
  akkaDData,
  playJson,
  fasterxml,
  sbtIO,
  akkaTestkit,
  finagle,
  junit,
  scalatest) ++ ckites
