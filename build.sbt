name := """scala-web-project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  evolutions,
  "com.typesafe.play" %% "play-json" % "2.6.0-M3",
  "joda-time" % "joda-time" % "2.9.6",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "org.postgresql" % "postgresql" % "9.4.1207.jre7",
  "org.scalikejdbc" %% "scalikejdbc"       % "2.5.0",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "2.5.0",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.3",
  "de.svenkubiak" % "jBCrypt" % "0.4.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % "test",
  "org.mockito" % "mockito-core" % "2.7.13" % "test"
)
