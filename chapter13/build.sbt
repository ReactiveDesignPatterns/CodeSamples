import Build._

resolvers += "twitter" at "https://maven.twttr.com/"

libraryDependencies ++= Seq(
  akkaContrib,
  akkaDData,
  playJson,
  finagle,
  fasterxml,
  sbtIO,
  akkaTestkit,
  junit,
  scalatest) ++ ckites
