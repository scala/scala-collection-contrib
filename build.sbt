// TODO Make it a cross project when Scala.js is released for 2.13.0-M4

scalaVersion := "2.13.0-M4"

organization := "org.scala-lang"

version := "0.1.0-SNAPSHOT"

scalacOptions ++= Seq("-deprecation", "-feature", "-opt-warnings", "-unchecked", "-language:higherKinds")

scalacOptions in (Compile, doc) ++= Seq("-implicits", "-groups")

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

fork in Test := true

parallelExecution in Test := false

homepage := Some(url("https://github.com/scala/scala-collection-contrib"))
 
licenses := Seq("BSD 3-clause" -> url("http://opensource.org/licenses/BSD-3-Clause"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/scala/scala-collection-contrib"),
    "scm:git:git@github.com:scala/scala-collection-contrib.git"
  )
)

pomExtra :=
    <developers>
      <developer><id>julienrf</id><name>Julien Richard-Foy</name></developer>
      <developer><id>szeiger</id><name>Stefan Zeiger</name></developer>
    </developers>

// For publishing snapshots
credentials ++= (
  for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)
).toList

libraryDependencies ++= Seq(
  "junit"            % "junit"           % "4.12",
  "com.novocode"     % "junit-interface" % "0.11"   % Test,
  "org.openjdk.jol"  % "jol-core"        % "0.5"
)

