import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val collectionContrib = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("collection-contrib"))
  .settings(ScalaModulePlugin.scalaModuleSettings)
  // as per #71, we are not currently attempting to support OSGi in this repo
  .disablePlugins(SbtOsgi)
  // disabling the plugin isn't sufficient; we also must refrain from letting sbt-scala-module put
  // OSGi stuff in our settings. normally a module build would include the following commented-out
  // line.  at present (sbt-scala-module 2.1.3), scalaModuleSettingsJVM contains *only* OSGi stuff,
  // so the easiest thing is just to omit it.  this isn't future-proof; some future sbt-scala-module
  // might put additional stuff in scalaModuleSettingsJVM. it would be nice if sbt-scala-module
  // provided a setting key to selectively disable OSGi.
  // .jvmSettings(ScalaModulePlugin.scalaModuleSettingsJVM)
  .settings(
    name := "scala-collection-contrib",
    scalacOptions ++= Seq("-opt-warnings", "-language:higherKinds", "-deprecation", "-feature", "-Xfatal-warnings"),
    scalacOptions in (Compile, doc) ++= Seq("-implicits", "-groups"),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    parallelExecution in Test := false,  // why?
    libraryDependencies ++= Seq(
      "junit"            % "junit"           % "4.12"   % Test,
      "com.novocode"     % "junit-interface" % "0.11"   % Test,
      "org.openjdk.jol"  % "jol-core"        % "0.9"    % Test
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
