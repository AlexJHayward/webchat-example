name := "webchat"

version := "1.1"
developers += Developer("AlexJHayward",
                        "Alex Hayward",
                        "@AlexJHayward",
                        url("https://github.com/AlexJHayward"))
description := "Basic web chat app for redis and play, built with scala, akka and javascript"
licenses := List(
  "WTFPL" → url("https://github.com/jslicense/WTFPL/blob/master/WTFPL"))

scalaVersion := "2.12.2"

lazy val `webchat` = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/")

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "com.github.etaty" %% "rediscala" % "1.8.0"
)

unmanagedResourceDirectories in Test <+= baseDirectory(
  _ / "target/web/public/test")
