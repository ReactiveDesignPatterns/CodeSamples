organization in ThisBuild := "com.reactivedesignpatterns"

scalaVersion in ThisBuild := "2.11.2"

lazy val common = project in file("common")

lazy val chapter02 = project dependsOn (common)

lazy val chapter04 = project dependsOn (common)

lazy val chapter07 = project dependsOn (common)

lazy val chapter12 = project dependsOn (common)
