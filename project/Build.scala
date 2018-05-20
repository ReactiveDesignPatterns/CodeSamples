import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.{HeaderLicense, headerLicense}
import sbt.{Def, _}
import scalafix.sbt.ScalafixPlugin.autoImport._
import scalariform.formatter.preferences._


object Build {
  val akkaVersion = "2.4.20"
  val akka25Version = "2.5.12"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akka25Actor = "com.typesafe.akka" %% "akka-actor" % akka25Version
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akka25Version % "test"
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion
  val akkaSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
  val akkaDData = "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaTyped = "com.typesafe.akka" %% "akka-typed-experimental" % akkaVersion
  val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  val akkaPersistenceQuery = "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion

  val levelDb = "org.iq80.leveldb" % "leveldb" % "0.7"

  val amazonAWS = "com.amazonaws" % "aws-java-sdk" % "1.11.109"

  val sbtIO = "org.scala-sbt" %% "io" % "1.1.3"

  val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.7"
  val scalaJava8 = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"

  val rxJava = "io.reactivex.rxjava2" % "rxjava" % "2.1.7"

  val playJson = "com.typesafe.play" %% "play-json" % "2.6.8"

  val scalatest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"

  val junit = "junit" % "junit" % "4.11" % "test"

  val guava = "com.google.guava" % "guava" % "23.0"

  //
  //  private val headerSettings = Seq(
  //    startYear := Some(2018),
  //    organization := "https://www.reactivedesignpatterns.com/ & http://rdp.reactiveplatform.xyz/"
  //  )


  private def setPreferences(preferences: IFormattingPreferences): IFormattingPreferences = preferences
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentConstructorArguments, false)
    .setPreference(DoubleIndentMethodDeclaration, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(NewlineAtEndOfFile, true)

  private val formats: Seq[Def.Setting[IFormattingPreferences]] = Seq(
    ScalariformKeys.preferences := setPreferences(ScalariformKeys.preferences.value),
    ScalariformKeys.preferences in Compile := setPreferences(ScalariformKeys.preferences.value),
    ScalariformKeys.preferences in Test := setPreferences(ScalariformKeys.preferences.value)
  )

  private val fixes = scalafixSettings ++ scalafixConfigure(Compile)

  val sharedSettings: Seq[Def.Setting[_]] = formats ++ fixes ++ Seq(
    headerLicense := Some(HeaderLicense.Custom(
      s"""|Copyright (c) 2018 https://www.reactivedesignpatterns.com/ ${"\n"}
          |Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
          |
          |""".stripMargin
    )
    ))

}
