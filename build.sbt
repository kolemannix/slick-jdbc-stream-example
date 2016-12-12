name := "slick-jdbc-stream-example"

version := "1.0"

val slickVersion = "3.1.0"
val akkaVersion = "2.4.14"
val akkaPackage = "com.typesafe.akka"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  akkaPackage %% "akka-actor" % akkaVersion,
  akkaPackage %% "akka-stream" % akkaVersion,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.7"

)

scalaVersion := "2.11.8"
    