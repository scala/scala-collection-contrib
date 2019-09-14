scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings")

val scalajsVersion = Option(System.getenv("SCALAJS_VERSION")).filter(_.nonEmpty).getOrElse("0.6.28")

addSbtPlugin("org.scala-lang.modules" % "sbt-scala-module" % "2.1.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalajsVersion)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")
