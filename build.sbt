organization in ThisBuild := "com.reactivedesignpatterns"
version in ThisBuild := "0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.6"

lazy val common = project

lazy val chapter02 = project dependsOn (common)

lazy val chapter04 = project dependsOn (common)

lazy val chapter07 = project dependsOn (common)

lazy val chapter12 = project dependsOn (common)
