name := """scala-web-project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  evolutions,
  ws,
  "org.scaldi" %% "scaldi" % "0.5.7",
  "org.scaldi" %% "scaldi-play" % "0.5.13-play-2.5.0-M2",
  "org.scalikejdbc" %% "scalikejdbc"        % "2.3.5",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.3.5",
  "com.h2database"  %  "h2"                % "1.4.191",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.3",
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
