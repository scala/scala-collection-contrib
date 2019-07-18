scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2")
addSbtPlugin("org.scala-lang.modules" % "sbt-scala-module" % "2.0.0")
