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

  private val fixes = scalafixSettings ++ Seq(
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List(
      "-Yrangepos", // required by SemanticDB compiler plugin
      "-Ywarn-unused-import" // required by `RemoveUnused` rule
    )
  )

  val sharedSettings: Seq[Def.Setting[_]] = formats ++ fixes ++ Seq(
    headerLicense := Some(HeaderLicense.Custom(
      s"""|Copyright (c) 2018 https://www.reactivedesignpatterns.com/ ${"\n"}
          |Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
          |
          |""".stripMargin
    )),
    scalaVersion := "2.12.8"

    //    ,
    //    wartremoverErrors ++= Warts.allBut(
    //      Wart.Any,
    //      Wart.FinalCaseClass,
    //      Wart.Null)
  )
}
