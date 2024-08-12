val zioSbtVersion = "0.4.0-alpha.28"

addSbtPlugin(
  "dev.zio"                     % "zio-sbt-ecosystem" % zioSbtVersion exclude ("org.scala-js", "sbt-scalajs") exclude ("org.scala-native", "sbt-scala-native")
)
addSbtPlugin(
  "dev.zio"                     % "zio-sbt-website"   % zioSbtVersion exclude ("org.scala-js", "sbt-scalajs") exclude ("org.scala-native", "sbt-scala-native")
)
addSbtPlugin(
  "dev.zio"                     % "zio-sbt-ci"        % zioSbtVersion exclude ("org.scala-js", "sbt-scalajs") exclude ("org.scala-native", "sbt-scala-native")
)
addSbtPlugin("com.typesafe"     % "sbt-mima-plugin"   % "1.1.1")
addSbtPlugin("org.scala-js"     % "sbt-scalajs"       % "1.16.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native"  % "0.5.4")

resolvers ++= Resolver.sonatypeOssRepos("public")

import sbt.internal.librarymanagement.mavenint.PomExtraDependencyAttributes

ThisBuild / dependencyOverrides ++= List(
  "org.scala-js"     % "sbt-scalajs"      % "1.16.0",
  "org.scala-native" % "sbt-scala-native" % "0.5.4"
).map(
  _.extra(
    PomExtraDependencyAttributes.SbtVersionKey   -> (update / scalaBinaryVersion).value,
    PomExtraDependencyAttributes.ScalaVersionKey -> sbtVersion.value
  )
)
