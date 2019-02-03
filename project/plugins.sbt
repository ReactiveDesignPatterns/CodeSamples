resolvers += "Bintray Jcenter" at "https://jcenter.bintray.com/"

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.2")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.4.2")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-theme" % "0.4.2")

addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.1.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.4.1")

addSbtPlugin("io.spray" % "sbt-boilerplate" % "0.6.1")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")
