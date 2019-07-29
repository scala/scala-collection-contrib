import sbtcrossproject.CrossPlugin.autoImport.{ crossProject, CrossType }
import ScalaModulePlugin._

lazy val root = project.in(file("."))
  .aggregate(`scala-collection-contribJS`, `scala-collection-contribJVM`)
  .settings(
    disablePublishing,
    // HACK If we don’t add this dependency the tests compilation of the aggregated projects fails
    libraryDependencies += "junit" % "junit" % "4.12" % Test
  )

lazy val `scala-collection-contrib` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform).in(file("."))
  .settings(scalaModuleSettings)
  .jvmSettings(scalaModuleSettingsJVM)
  .settings(
    name := "scala-collection-contrib",
    version := "0.1.1-SNAPSHOT",

    crossScalaVersions in ThisBuild := Seq("2.13.0"),

    scalacOptions ++= Seq("-opt-warnings", "-language:higherKinds", "-deprecation", "-feature", "-Xfatal-warnings"),
    scalacOptions in (Compile, doc) ++= Seq("-implicits", "-groups"),

    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    parallelExecution in Test := false,  // why?

    mimaPreviousVersion := Some("0.1.0"),

    homepage := Some(url("https://github.com/scala/scala-collection-contrib")),
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),

    libraryDependencies ++= Seq(
      "junit"            % "junit"           % "4.12"   % Test,
      "com.novocode"     % "junit-interface" % "0.11"   % Test,
      "org.openjdk.jol"  % "jol-core"        % "0.9"    % Test
    )
  )
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .jsSettings(
    mimaPreviousVersion := None,
    // Scala.js cannot run forked tests
    fork in Test := false
  )

lazy val `scala-collection-contribJVM` = `scala-collection-contrib`.jvm
lazy val `scala-collection-contribJS` = `scala-collection-contrib`.js
