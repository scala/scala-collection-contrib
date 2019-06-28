// TODO Make it a cross project including Scala.js
import ScalaModulePlugin._

scalaModuleSettings

name := "scala-collection-contrib"

version := "0.1.0-SNAPSHOT"

scalaVersionsByJvm in ThisBuild := {
	val v213 = "2.13.0"
	Map(
		8 -> List(v213 -> true),
		11 -> List(v213 -> false),
		12 -> List(v213 -> false))
}

scalacOptions ++= Seq("-opt-warnings", "-language:higherKinds")

scalacOptions in (Compile, doc) ++= Seq("-implicits", "-groups")

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

parallelExecution in Test := false

homepage := Some(url("https://github.com/scala/scala-collection-contrib"))

licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

pomExtra :=
    <developers>
      <developer><id>julienrf</id><name>Julien Richard-Foy</name></developer>
      <developer><id>szeiger</id><name>Stefan Zeiger</name></developer>
    </developers>

libraryDependencies ++= Seq(
  "junit"            % "junit"           % "4.12",
  "com.novocode"     % "junit-interface" % "0.11"   % Test,
  "org.openjdk.jol"  % "jol-core"        % "0.9"
)
