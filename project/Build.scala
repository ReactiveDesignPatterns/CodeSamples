import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.{HeaderLicense, headerLicense}
import sbt.{Def, _}
import scalafix.sbt.ScalafixPlugin.autoImport._
import scalariform.formatter.preferences._


object Build {
  val akkaVersion = "2.4.20"
  val akka25Version = "2.5.16"
  val ckiteVersion = "0.2.1"
  val scalaVersion = "2.12.6"

  val akka25Actor = "com.typesafe.akka" %% "akka-actor" % akka25Version

  val akka25Testkit = "com.typesafe.akka" %% "akka-testkit" % akka25Version % "test"

  val akka25Contrib = "com.typesafe.akka" %% "akka-contrib" % akka25Version

  val akka25Sharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akka25Version

  val akka25DData = "com.typesafe.akka" %% "akka-distributed-data" % akka25Version

  val akka25Stream = "com.typesafe.akka" %% "akka-stream" % akka25Version

  val akkaTyped = "com.typesafe.akka" %% "akka-typed-experimental" % akkaVersion

  val akka25Typed = "com.typesafe.akka" %% "akka-actor-typed" % akka25Version

  val akka25Persistence = "com.typesafe.akka" %% "akka-persistence" % akka25Version

  val akka25PersistenceQuery = "com.typesafe.akka" %% "akka-persistence-query" % akka25Version

  val levelDb = "org.iq80.leveldb" % "leveldb" % "0.10"

  val amazonAWS = "com.amazonaws" % "aws-java-sdk" % "1.11.358"

  val sbtIO = "org.scala-sbt" %% "io" % "1.1.10"

  val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.7"
  val scalaJava8 = "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"

  val rxJava = "io.reactivex.rxjava2" % "rxjava" % "2.1.16"

  val playJson = "com.typesafe.play" %% "play-json" % "2.6.9"

  val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"

  val junit = "junit" % "junit" % "4.12" % "test"

  val guava = "com.google.guava" % "guava" % "23.0"

  val finagle = "com.twitter" %% "finagle-http" % "18.6.0"
  val fasterxml = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.6"

  val ckites = Seq(
    "io.ckite" % "ckite-core" % ckiteVersion exclude("org.apache.thrift", "libthrift"),
    "io.ckite" % "ckite-finagle" % ckiteVersion exclude("org.apache.thrift", "libthrift"),
    "io.ckite" % "ckite-mapdb" % ckiteVersion exclude("org.apache.thrift", "libthrift")
  )

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
    )
//    ,
//    wartremoverErrors ++= Warts.allBut(
//      Wart.Any,
//      Wart.FinalCaseClass,
//      Wart.Null)
  )
}
