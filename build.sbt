import com.typesafe.sbt.SbtGit.GitKeys
import com.typesafe.sbt.git.DefaultReadableGit

organization in ThisBuild := "com.reactivedesignpatterns"

name := "ReactiveDesignPatterns"

version in ThisBuild := "1.0.0"

scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.8", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")

javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-XDignore.symbol.file")

conflictWarning := conflictWarning.value.copy(failOnConflict = false)

//javaFormattingSettingsFilename in ThisBuild := "formatting-java.xml"

//sourceLevel in ThisBuild := Some("1.8")

//targetLevel in ThisBuild := Some("1.8")

enablePlugins(spray.boilerplate.BoilerplatePlugin)

enablePlugins(AutomateHeaderPlugin)

lazy val ReactiveDesignPatterns = (project in file("."))
  .aggregate(docs)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val common = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val chapter02 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.akka25Actor,
      Dependencies.guava
    )
  )

lazy val chapter03 = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val chapter07 = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val chapter11 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.akka25Testkit,
      Dependencies.scalatest,
      Dependencies.scalaAsync
    )
  )

lazy val chapter12 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.akka25Actor
    )
  )

lazy val chapter13 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
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
  )

lazy val chapter14 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val chapter15 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val chapter16 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

lazy val chapter17 = project.dependsOn(common)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(Build.sharedSettings: _*)

headerLicense := Some(HeaderLicense.Custom(
  s"""|Copyright (c) 2018 https://www.reactivedesignpatterns.com/ ${"\n"}
      |Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
      |
     |""".stripMargin
))

lazy val docs = project.aggregate(
  chapter02,
  chapter03,
  chapter07,
  chapter11,
  chapter12,
  chapter13,
  chapter14,
  chapter15,
  chapter16,
  chapter17)
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(ParadoxMaterialThemePlugin)
  .settings(Build.sharedSettings: _*)
  .settings(
    git.remoteRepo := "https://github.com/ReactivePlatform/reactive-design-patterns-cn.git",

    GitKeys.gitReader in ThisProject := baseDirectory(base => new DefaultReadableGit(base)).value,

    excludeFilter in ghpagesCleanSite :=
      new FileFilter {
        def accept(f: File) = (ghpagesRepository.value / "CNAME").getCanonicalPath == f.getCanonicalPath
      } || "versions.html",


    ParadoxMaterialThemePlugin.paradoxMaterialThemeSettings(Paradox),

    paradoxProperties in Compile ++= Map(
      "project.name" -> "ReactiveDesignPatterns",
      "github.base_url" -> "https://github.com/ReactivePlatform/reactive-design-patterns-cn"
    ),

    paradoxMaterialTheme in Compile ~= {
      _.withColor("red", "pink")
        .withLogoIcon("cloud")
        .withCopyright("Copyleft © 2018 rdp.reactiveplatform.xyz")
        .withRepository(uri("https://github.com/ReactivePlatform/reactive-design-patterns-cn"))
        .withSearch(tokenizer = "[\\s\\-\\.]+")
        .withSocial(
          uri("https://github.com/hepin1989")
        )
    }
  )


