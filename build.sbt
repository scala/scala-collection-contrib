ThisBuild / scalaVersion := "2.13.8"

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
    versionPolicyIntention := Compatibility.BinaryCompatible,
    scalaModuleAutomaticModuleName := Some("scala.collection.contrib"),
    Compile / compile / scalacOptions ++= Seq("-opt-warnings", "-language:higherKinds", "-deprecation", "-feature", "-Xfatal-warnings", "-Wconf:origin=scala.collection.IterableOps.toIterable:s"),
    Compile / doc / scalacOptions ++= Seq("-implicits", "-groups", "-nowarn"),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    Test / parallelExecution := false,  // why?
    libraryDependencies ++= Seq(
      "junit"            % "junit"           % "4.13.2" % Test,
      "com.github.sbt"   % "junit-interface" % "0.13.3" % Test,
    ),
  )
  .jsEnablePlugins(ScalaJSJUnitPlugin)
  .jsSettings(
    // Scala.js cannot run forked tests
    Test / fork := false
  )

lazy val collectionContribJVM = collectionContrib.jvm
lazy val collectionContribJS  = collectionContrib.js
