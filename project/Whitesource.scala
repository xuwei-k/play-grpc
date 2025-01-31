/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

package build.play.grpc

import sys.process.Process

import sbt._
import sbt.Keys._
import sbtwhitesource.WhiteSourcePlugin.autoImport._
import sbtwhitesource._

object Whitesource extends AutoPlugin {
  lazy val gitCurrentBranch =
    Process("git rev-parse --abbrev-ref HEAD").!!.replaceAll("\\s", "")

  override def requires = WhiteSourcePlugin

  override def trigger = allRequirements

  override lazy val projectSettings = Seq(
    // do not change the value of whitesourceProduct
    whitesourceProduct := "Lightbend Reactive Platform",
    whitesourceAggregateProjectName := {
      val projectName = (LocalRootProject / moduleName).value.replace("-root", "")
      projectName + "-" + (if (isSnapshot.value)
                             if (gitCurrentBranch == "master") "master"
                             else "adhoc"
                           else
                             CrossVersion
                               .partialVersion((LocalRootProject / version).value)
                               .map { case (major, minor) => s"$major.$minor-stable" }
                               .getOrElse("adhoc"))
    },
    whitesourceForceCheckAllDependencies := true,
    whitesourceFailOnError := true,
  )
}
