// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app

import at.geoheil.utils.SparkBaseRunner
import org.apache.spark.sql.{ DataFrame, SparkSession }

object SparkJob extends SparkBaseRunner {
  val spark = createSparkSession(this.getClass.getName)

  import spark.implicits._
  // TODO businesss logic here

  val df = Seq(
    (0, "A", "B", "C", "D"),
    (1, "A", "B", "C", "D"),
    (0, "d", "a", "jkl", "d"),
    (0, "d", "g", "C", "D"),
    (1, "A", "d", "t", "k"),
    (1, "d", "c", "C", "D"),
    (1, "c", "B", "C", "D")).toDF("TARGET", "col1", "col2", "col3TooMany", "col4")
  df.createOrReplaceTempView("mydf")
  spark.sql(
    """
      |SELECT * FROM mydf
    """.stripMargin).show

  df.show

  //  val homeDir = System.getProperty("user.home")
  //  var path = homeDir + File.separator + "directory" + File.separator
  //  path = path.replaceFirst("^~", System.getProperty("user.home"))

  //  val rawDf = readCsv(spark, path + "pathToFile")
  spark.stop

  def readCsv(spark: SparkSession, inputPath: String): DataFrame = {
    //    import spark.implicits._
    spark.read.
      option("header", "true")
      .option("inferSchema", true)
      .option("charset", "UTF-8")
      .option("delimiter", ";")
      .csv(inputPath)
      .withColumn("col1", $"col1".cast("Date"))
      .withColumnRenamed("col2", "colAAA")
  }
}
