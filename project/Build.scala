import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.{HeaderLicense, headerLicense}
import sbt.{Def, _}
import scalafix.sbt.ScalafixPlugin.autoImport._
import scalariform.formatter.preferences._

object Build {

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

  import sbt.Keys._

  final val DefaultScalacOptions = Seq("-encoding", "UTF-8", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint", "-Ywarn-unused", "-deprecation",
    //    "-Xfatal-warnings"
    "-Yrangepos", // required by SemanticDB compiler plugin
    "-Ywarn-unused-import" // required by `RemoveUnused` rule
  )

  final val DefaultJavacOptions = Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-XDignore.symbol.file")

  val sharedSettings: Seq[Def.Setting[_]] = formats ++ Seq(
    addCompilerPlugin(scalafixSemanticdb),
    headerLicense := Some(HeaderLicense.Custom(
      s"""|Copyright (c) 2018 https://www.reactivedesignpatterns.com/ ${"\n"}
          |Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
          |
          |""".stripMargin
    )),
    scalaVersion := "2.12.8",
    conflictWarning := conflictWarning.value.copy(failOnConflict = false),
    scalacOptions in Compile := DefaultScalacOptions,
    scalacOptions in Test := DefaultScalacOptions,
    scalacOptions in Compile ++= (
      if (System.getProperty("java.version").startsWith("1."))
        Seq("-target:jvm-1.8")
      else if (scalaBinaryVersion.value == "2.11")
        Seq("-target:jvm-1.8", "-javabootclasspath", CrossJava.Keys.fullJavaHomes.value("8") + "/jre/lib/rt.jar")
      else
      // -release 8 is not enough, for some reason we need the 8 rt.jar explicitly #25330
        Seq("-release", "8", "-javabootclasspath", CrossJava.Keys.fullJavaHomes.value("8") + "/jre/lib/rt.jar")),
    scalacOptions in Test := (scalacOptions in Test).value.filterNot(opt ⇒
      opt == "-Xlog-reflective-calls" || opt.contains("genjavadoc")) ++ Seq(
      "-Ywarn-unused"),
    javacOptions in compile ++= DefaultJavacOptions ++ (
      if (System.getProperty("java.version").startsWith("1."))
        Seq()
      else
        Seq("-source", "8", "-target", "8", "-bootclasspath", CrossJava.Keys.fullJavaHomes.value("8") + "/jre/lib/rt.jar")
      ),
    javacOptions in test ++= DefaultJavacOptions ++ (
      if (System.getProperty("java.version").startsWith("1."))
        Seq()
      else
        Seq("-source", "8", "-target", "8", "-bootclasspath", CrossJava.Keys.fullJavaHomes.value("8") + "/jre/lib/rt.jar")
      ),

    //    ,
    //    wartremoverErrors ++= Warts.allBut(
    //      Wart.Any,
    //      Wart.FinalCaseClass,
    //      Wart.Null)
  )
}
