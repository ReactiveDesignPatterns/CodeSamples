import Build._

libraryDependencies ++= Seq(
  Dependencies.akka25Actor,
  Dependencies.akkaTyped,
  Dependencies.akka25Persistence,
  Dependencies.akka25PersistenceQuery,
  Dependencies.akka25Stream,
  Dependencies.junit,
  Dependencies.scalatest,
  Dependencies.levelDb)
