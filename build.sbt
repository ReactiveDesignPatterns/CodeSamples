organization in ThisBuild := "com.reactivedesignpatterns"
version in ThisBuild := "0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"

lazy val common = project

lazy val chapter02 = project dependsOn (common)

lazy val chapter07 = project dependsOn (common)

lazy val chapter11 = project dependsOn (common)

lazy val chapter12 = project dependsOn (common)

lazy val chapter13 = project dependsOn (common)

lazy val chapter14 = project dependsOn (common)

lazy val chapter15 = project dependsOn (common)

lazy val chapter16 = project dependsOn (common)

lazy val chapter17 = project dependsOn (common)
