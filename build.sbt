import Dependencies._

val Http4sVersion = "0.21.1"
val CirceVersion = "0.13.0"
val Specs2Version = "4.8.3"
val LogbackVersion = "1.2.3"

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.0.1-SNAPSHOT"
ThisBuild / organization     := "org.tulth.alexa_skill"
ThisBuild / organizationName := "alexa_skill"

lazy val root = (project in file("."))
  .settings(
    name := "alexa_skill",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server"   % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client"   % Http4sVersion,
      "org.http4s"      %% "http4s-circe"          % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"            % Http4sVersion,
      "io.circe"        %% "circe-generic"         % CirceVersion,
      "io.circe"        %% "circe-generic-extras"  % CirceVersion,
      "io.circe"        %% "circe-parser"          % CirceVersion,
      "org.specs2"      %% "specs2-core"           % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"       % LogbackVersion,
      scalaTest         %  Test
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

