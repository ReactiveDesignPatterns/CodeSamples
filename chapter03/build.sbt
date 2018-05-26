lazy val eventloop = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(headerLicense := Some(HeaderLicense.Custom(
    s"""|Copyright (c) 2018 https://www.reactivedesignpatterns.com/${"\n"}
        |Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
        |
       |""".stripMargin
  )
  ))

lazy val csp = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(headerLicense := Some(HeaderLicense.Custom(
    s"""|Copyright (c) 2018 https://www.reactivedesignpatterns.com/${"\n"}
        |Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
        |
       |""".stripMargin
  )
  ))

lazy val `futures-and-promises` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Build.junit,
      Build.scalaAsync
    )
  )

lazy val `reactiveExtensions` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Build.rxJava,
      Build.junit
    )
  )

lazy val actor = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
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
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25"
)