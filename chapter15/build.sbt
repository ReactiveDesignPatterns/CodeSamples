import Build._

libraryDependencies ++= Seq(
  akka25Actor,
  akkaTyped,
  akka25Persistence,
  akka25PersistenceQuery,
  akka25Stream,
  junit,
  scalatest,
  levelDb)
