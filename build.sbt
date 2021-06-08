lazy val root = project.in(file("."))
  .aggregate(collectionContrib.jvm, collectionContrib.js)
  .settings(
    publish / skip := true,
    // With CrossType.Pure, the root project also picks up the sources in `src`
    Compile / unmanagedSourceDirectories := Nil,
    Test    / unmanagedSourceDirectories := Nil,
  )

lazy val collectionContrib = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(ScalaModulePlugin.scalaModuleSettings)
  .settings(
    name := "scala-collection-contrib",
    scalaModuleAutomaticModuleName := Some("scala.collection.contrib"),
    scalacOptions ++= Seq("-opt-warnings", "-language:higherKinds", "-deprecation", "-feature", "-Xfatal-warnings"),
    scalacOptions in (Compile, doc) ++= Seq("-implicits", "-groups"),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    parallelExecution in Test := false,  // why?
    libraryDependencies ++= Seq(
      "junit"            % "junit"           % "4.13.2" % Test,
      "com.novocode"     % "junit-interface" % "0.11"   % Test,
    ),
  )
  .jvmSettings(
    scalaModuleMimaPreviousVersion := Some("0.1.0")
  )
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .jsSettings(
    // Scala.js cannot run forked tests
    fork in Test := false
  )

lazy val collectionContribJVM = collectionContrib.jvm
lazy val collectionContribJS  = collectionContrib.js
