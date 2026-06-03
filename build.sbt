/*
 * Copyright 2026 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import scala.sys.process.*
import creativescala.ExternalLink
import laika.config.LinkConfig
import laika.config.ApiLinks
import laika.theme.Theme
import laika.helium.config.TextLink

ThisBuild / tlBaseVersion := "0.5" // your current series x.y

scalaVersion := "3.8.3"

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "org.creativescala"
ThisBuild / organizationName := "Creative Scala"
ThisBuild / startYear := Some(2026)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("noelwelsh", "Noel Welsh")
)

lazy val scala3 = "3.8.3"

ThisBuild / crossScalaVersions := List(scala3)
ThisBuild / githubWorkflowJavaVersions := List(JavaSpec.temurin("17"))
ThisBuild / scalaVersion := scala3
ThisBuild / useSuperShell := true
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / tlJdkRelease := Some(17)
// Scala 3.8 / Capture checking related settings
// ThisBuild / scalacOptions += "-language:experimental.captureChecking"

// Run this (build) to do everything involved in building the project
commands += Command.command("build") { state =>
  "clean" ::
    "compile" ::
    "test" ::
    "scalafixAll" ::
    "scalafmtAll" ::
    "scalafmtSbt" ::
    "headerCreateAll" ::
    "dependencyUpdates" ::
    "reload plugins; dependencyUpdates; reload return" ::
    state
}

// Dependencies

val catsCore = Def.setting("org.typelevel" %%% "cats-core" % "2.13.0")

val munitVersion = "1.3.0"
val munit = Def.setting("org.scalameta" %%% "munit" % munitVersion % "test")
val munitScalaCheck =
  Def.setting("org.scalameta" %%% "munit-scalacheck" % munitVersion % "test")

// Projects and Settings

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    munit.value,
    munitScalaCheck.value
  ),
  startYear := Some(2026),
  licenses := List(
    "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  )
)

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("core"))
  .settings(
    commonSettings,
    libraryDependencies += "org.creativescala" %%% "terminus-core" % "0.5.0"
  )
