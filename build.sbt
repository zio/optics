enablePlugins(ZioSbtEcosystemPlugin, ZioSbtCiPlugin)

crossScalaVersions := Seq.empty

inThisBuild(
  List(
    name := "ZIO Optics",
    ciEnabledBranches := Seq("series/2.x"),
    developers := List(
      Developer(
        "jdegoes",
        "John De Goes",
        "john@degoes.net",
        url("http://degoes.net")
      ),
      Developer(
        "adamgfraser",
        "Adam Fraser",
        "adam.fraser@gmail.com",
        url("https://github.com/adamgfraser")
      )
    ),
    scala213 := "2.13.14"
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  ";zioOpticsJVM/test"
)
addCommandAlias(
  "testJS",
  ";zioOpticsJS/test"
)
addCommandAlias(
  "testNative",
  ";zioOpticsNative/test:compile"
)

val zioVersion = "2.1.7"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    unusedCompileDependenciesFilter -= moduleFilter("org.scala-js", "scalajs-library")
  )
  .aggregate(
    zioOptics.jvm,
    zioOptics.js,
    zioOptics.native,
    docs
  )

lazy val zioOptics = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("zio-optics"))
  .settings(stdSettings(name = Some("zio-optics"), packageName = Some("zio.optics"), enableCrossProject = true))
  .settings(enableZIO())
  .settings(
    libraryDependencies += "dev.zio" %%% "zio"          % zioVersion,
    libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test,
    libraryDependencies += "dev.zio" %%% "zio-test"     % zioVersion % Test
  )
  .jvmSettings(scala3Settings)
  .jvmSettings(scalaReflectTestSettings)
  .jsSettings(jsSettings)
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    scalacOptions ++= {
      if (scalaVersion.value == scala3.value) List("-scalajs") else List() // https://github.com/zio/zio-sbt/pull/150
    },
    excludeDependencies += ExclusionRule("org.portable-scala", "portable-scala-reflect_2.13")
  )
  .nativeSettings(nativeSettings)
  .nativeSettings(
    excludeDependencies += ExclusionRule("org.portable-scala", "portable-scala-reflect_2.13")
  )

lazy val docs = project
  .in(file("zio-optics-docs"))
  .settings(
    moduleName := "zio-optics-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    projectName := (ThisBuild / name).value,
    mainModuleName := (zioOptics.jvm / moduleName).value,
    projectStage := ProjectStage.Development,
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioOptics.jvm)
  )
  .dependsOn(zioOptics.jvm)
  .enablePlugins(WebsitePlugin)
