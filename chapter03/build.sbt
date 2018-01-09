lazy val eventloop = project

lazy val csp = project

lazy val `futures-and-promises` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings:_*)
  .settings(
      libraryDependencies ++= Seq(
        Build.junit,
        Build.scalaAsync
      )
    )

lazy val `reactiveExtensions` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings:_*)
    .settings(
      libraryDependencies ++= Seq(
        Build.rxJava,
        Build.junit
      )
    )

lazy val actor = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      Build.akka25Actor
    )
  )

lazy val chapter03 = (project in file("."))
  .aggregate(
    eventloop,
    csp,
    `futures-and-promises`,
    `reactiveExtensions`,
    actor)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25"
)