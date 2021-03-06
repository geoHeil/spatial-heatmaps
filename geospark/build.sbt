name := "sparkMiniSample"
organization := "myOrg"

scalaVersion := "2.11.12"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Xlint:missing-interpolator",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)

//The default SBT testing java options are too small to support running many of the tests
// due to the need to launch Spark in local mode.
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled")
parallelExecution in Test := false

lazy val spark = "2.2.0"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
resolvers += Resolver.mavenLocal
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % spark % "provided",
  "org.apache.spark" %% "spark-sql" % spark % "provided",
  "org.apache.spark" %% "spark-hive" % spark % "provided",
  //  "graphframes" % "graphframes" % "0.5.0-spark2.1-s_2.11",
  //  "org.apache.spark" %% "spark-graphx" % spark % "provided",
  //  "org.apache.spark" %% "spark-mllib" % spark % "provided",
  //  "org.apache.spark" %% "spark-streaming" % spark % "provided",

  // spatial stuff
  "org.datasyslab" % "babylon" % "0.2.2",
  "org.datasyslab" % "geospark" % "0.9.1",

  //  typesafe configuration
  "com.github.pureconfig" %% "pureconfig" % "0.8.0",

  // testing
  "com.holdenkarau" %% "spark-testing-base" % s"${spark}_0.8.0" % "test"
)
fork := true
fullClasspath in reStart := (fullClasspath in Compile).value
run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass.in(Compile, run), runner.in(Compile, run)).evaluated

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "LICENSE") => MergeStrategy.discard
  case PathList("META-INF", "LICENSE.txt") => MergeStrategy.discard
  case PathList("META-INF", "NOTICE") => MergeStrategy.discard
  case PathList("META-INF", "NOTICE.txt") => MergeStrategy.discard
  case PathList("META-INF", "com.fasterxml.jackson", xs@_*) => MergeStrategy.first
  case PathList("rootdoc.txt") => MergeStrategy.discard
//  case PathList("com.fasterxml.jackson", xs@_*) => MergeStrategy.first // geospark & babylon dirty fix
//  case _ => MergeStrategy.deduplicate
  case _ => MergeStrategy.first //TODO dirty fix https://github.com/jiayuasu/GeoSparkTemplateProject/issues/1
}

assemblyShadeRules in assembly := Seq(ShadeRule.rename("shapeless.**" -> "new_shapeless.@1").inAll)

test in assembly := {}

initialCommands in console :=
  """
    |import at.geoheil.utils.SparkBaseRunner
    |import org.slf4j.LoggerFactory
    |import org.apache.spark.sql.{ DataFrame, SparkSession }
    |import org.apache.spark.sql.functions._
    |import at.geoheil.utils.{SampleConfig, ConfigurationUtils}
    |
    |val logger = LoggerFactory.getLogger(this.getClass)
    |val c = ConfigurationUtils.loadConfiguration[SampleConfig]
    |val spark = ConfigurationUtils.createSparkSession("console")
    |import spark.implicits._
  """.stripMargin

mainClass := Some("at.geoheil.SparkJob")