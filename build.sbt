import com.typesafe.sbt.SbtGit.GitKeys
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.git.DefaultReadableGit

import scalariform.formatter.preferences._

organization in ThisBuild := "com.reactivedesignpatterns"

name := "ReactiveDesignPatterns"

version in ThisBuild := "0.1-SNAPSHOT"

scalaVersion := "2.12.4"

scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.8", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")

javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-XDignore.symbol.file")

resolvers += "akka" at "https://dl.bintray.com/akka/maven/"

enablePlugins(spray.boilerplate.BoilerplatePlugin)

enablePlugins(AutomateHeaderPlugin)

startYear := Some(2017)

licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

def setPreferences(preferences: IFormattingPreferences): IFormattingPreferences = preferences
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, false)
  .setPreference(DoubleIndentMethodDeclaration, false)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(NewlineAtEndOfFile, true)

ScalariformKeys.preferences := setPreferences(ScalariformKeys.preferences.value)
ScalariformKeys.preferences in Compile := setPreferences(ScalariformKeys.preferences.value)
ScalariformKeys.preferences in Test := setPreferences(ScalariformKeys.preferences.value)


lazy val ReactiveDesignPatterns = (project in file(".")).dependsOn(docs)

lazy val common = project

lazy val chapter02 = project

lazy val chapter07 = project

lazy val chapter11 = project dependsOn common

lazy val chapter12 = project

lazy val chapter13 = project dependsOn common

lazy val chapter14 = project dependsOn common

lazy val chapter15 = project dependsOn common

lazy val chapter16 = project dependsOn common

lazy val chapter17 = project dependsOn common

lazy val docs = project.aggregate(
  chapter02,
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
        .withCopyright("Copyleft © 2017 rdp.reactiveplatform.xyz")
        .withRepository(uri("https://github.com/ReactivePlatform/reactive-design-patterns-cn"))
        .withSearch(tokenizer = "[\\s\\-\\.]+")
        .withSocial(
          uri("https://github.com/hepin1989")
        )
    }
  )

scalafixSettings

scalafixConfigure(Compile)
