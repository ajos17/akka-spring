name := "seed_nodes"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.9" % Test,
  "com.typesafe.akka" %% "akka-cluster" % "2.5.9",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.9",
  "com.typesafe.akka" %% "akka-http"   % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "org.springframework.security" % "spring-security-core" % "4.2.3.RELEASE",
  "org.springframework.security" % "spring-security-web" % "4.2.3.RELEASE",
  "org.springframework.security" % "spring-security-config" % "4.2.3.RELEASE",
  "org.springframework.security" % "spring-security-taglibs" % "4.2.3.RELEASE",
  "org.springframework" % "spring-context" % "4.3.9.RELEASE",
  "org.springframework" % "spring-web" % "4.3.9.RELEASE"

)



    