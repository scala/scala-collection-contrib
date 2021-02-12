val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).filter(_.nonEmpty).getOrElse("1.5.0")

addSbtPlugin("org.scala-lang.modules" % "sbt-scala-module" % "2.2.4")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
