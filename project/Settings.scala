import sbt.Keys._

object Settings {

  val moduleSettings = Seq(

    organization := "io.brunofitas",

    version := "1.0",

    scalaVersion := Library.version("scala"),

    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:postfixOps",
      "-Xlint"
    ),

    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8",
      "Xlint"
    ),


    libraryDependencies ++= Seq(
      Library.scalaTest
    ),

    resolvers ++= Library.resolvers,

    fork in run := true

  )

}
