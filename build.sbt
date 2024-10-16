ThisBuild / version := "1.7.2"

ThisBuild / scalaVersion := "2.12.17"

ThisBuild / libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.2",
  "org.apache.spark" %% "spark-sql" % "3.2.2",
  "org.apache.spark" %% "spark-mllib" % "3.2.2",
  "com.datastax.oss" % "java-driver-core" % "4.13.0",
  "com.datastax.oss" % "java-driver-core-shaded" % "4.13.0",
  "com.github.jnr" % "jnr-posix" % "3.1.15",
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.2.0"
)

// Attention, le connecteur Cassandra n'est valable que jusque scala 2.12 et Spark 3.2.2

lazy val root = (project in file("."))
  .settings(
    name := "mySparkProject"
  )
