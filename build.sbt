import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

// With CrossType.Pure, the root project also picks up the sources in `src`
Compile/sources := Nil
Test/sources := Nil

lazy val collectionContrib = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(ScalaModulePlugin.scalaModuleSettings)
  .settings(
    name := "scala-collection-contrib",
    scalacOptions ++= Seq("-opt-warnings", "-language:higherKinds", "-deprecation", "-feature", "-Xfatal-warnings"),
    scalacOptions in (Compile, doc) ++= Seq("-implicits", "-groups"),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    parallelExecution in Test := false,  // why?
    libraryDependencies ++= Seq(
      "junit"            % "junit"           % "4.13"   % Test,
      "com.novocode"     % "junit-interface" % "0.11"   % Test,
      "org.openjdk.jol"  % "jol-core"        % "0.10"    % Test
    ),
    // https://github.com/sbt/sbt/issues/5043
    useCoursier := false
  )
  .jvmSettings(
    scalaModuleMimaPreviousVersion := Some("0.1.0")
  )
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .jsSettings(
    // Scala.js cannot run forked tests
    fork in Test := false
  )
