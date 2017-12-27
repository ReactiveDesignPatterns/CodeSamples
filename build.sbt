organization in ThisBuild := "com.reactivedesignpatterns"
version in ThisBuild := "0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.4"

enablePlugins(spray.boilerplate.BoilerplatePlugin)

lazy val ReactiveDesignPatterns = (project in file("."))
.aggregate(
  chapter02,
  chapter07,
  chapter11,
  chapter12,
  chapter13,
  chapter14,
  chapter15,
  chapter16,
  chapter17)


lazy val common = project

lazy val chapter02 = project

lazy val chapter07 = project

lazy val chapter11 = project dependsOn common

lazy val chapter12 = project

lazy val chapter13 = project dependsOn (common)

lazy val chapter14 = project dependsOn (common)

lazy val chapter15 = project dependsOn (common)

lazy val chapter16 = project dependsOn (common)

lazy val chapter17 = project dependsOn (common)
