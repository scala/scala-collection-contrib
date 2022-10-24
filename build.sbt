ThisBuild / scalaVersion := "3.1.3"
ThisBuild / crossScalaVersions := Seq((ThisBuild / scalaVersion).value, "2.13.8")

lazy val root = project.in(file("."))
  .aggregate(collectionContrib.jvm, collectionContrib.js, collectionContrib.native)
  .settings(
    publish / skip := true,
    // With CrossType.Pure, the root project also picks up the sources in `src`
    Compile / unmanagedSourceDirectories := Nil,
    Test    / unmanagedSourceDirectories := Nil,
  )

lazy val collectionContrib = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(ScalaModulePlugin.scalaModuleSettings)
  .settings(
    name := "scala-collection-contrib",
    versionPolicyIntention := Compatibility.None,
    scalaModuleAutomaticModuleName := Some("scala.collection.contrib"),
    Compile / compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => Seq("-opt-warnings", "-Werror", "-Wconf:origin=scala.collection.IterableOps.toIterable:s")
        case _            => Seq("-Xfatal-warnings", /*"-scala-output-version:3.0",*/ "-Wconf:cat=deprecation:s")
      }
    },
    Compile / doc / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => Seq("-implicits", "-groups", "-nowarn")
        case _            => Seq.empty
      }
    },
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
  .nativeEnablePlugins(ScalaNativeJUnitPlugin)
  .nativeSettings(
    Test / fork := false // is this needed?
  )

lazy val collectionContribJVM    = collectionContrib.jvm
lazy val collectionContribJS     = collectionContrib.js
lazy val collectionContribNative = collectionContrib.native
