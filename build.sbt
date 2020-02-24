import Dependencies._

val CirceVersion = "0.13.0"

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "org.tulth.alexa_skill"
ThisBuild / organizationName := "alexa_skill"

lazy val root = (project in file("."))
  .settings(
    name := "alexa_skill_json",
    libraryDependencies ++= Seq(
      "io.circe"        %% "circe-generic"         % CirceVersion,
      "io.circe"        %% "circe-generic-extras"  % CirceVersion,
      "io.circe"        %% "circe-parser"          % CirceVersion,
      scalaTest % Test
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)

