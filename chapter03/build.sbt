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
      Dependencies.junit,
      Dependencies.scalaAsync
    )
  )

lazy val `reactiveExtensions` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.rxJava,
      Dependencies.junit
    )
  )

lazy val actor = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.akka25Actor
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
  "org.slf4j" % "slf4j-api" % "1.7.26"
)