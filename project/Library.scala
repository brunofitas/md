import sbt.{Resolver, _}

object Library {

  val resolvers = Seq(
    Resolver sonatypeRepo "public",
    Resolver typesafeRepo "releases"
  )

  val version = Map(
    "scala"       -> "2.12.4",
    "akka"        -> "2.5.9",
    "akkaHttp"    -> "10.0.10",
    "scalaTest"   -> "3.0.3"
  )

  val akkaActor         = "com.typesafe.akka" %% "akka-actor"   % version("akka")
  val akkaTestKit       = "com.typesafe.akka" %% "akka-testkit" % version("akka") % Test
  val akkaStream        = "com.typesafe.akka" %% "akka-stream"  % version("akka")
  val akkaSlf4j         = "com.typesafe.akka" %% "akka-slf4j"   % version("akka")
  val akkaHttp          = "com.typesafe.akka" %% "akka-http"    % version("akkaHttp")
  val scalaTest         = "org.scalatest"     %% "scalatest"    % version("scalaTest") % Test

}
