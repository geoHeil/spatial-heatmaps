// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app

import at.geoheil.utils.{ HiveUtils, SparkBaseRunner }
import org.apache.spark.sql.functions._

import scala.language.postfixOps

object SparkJob extends SparkBaseRunner {
  val spark = createSparkSession(this.getClass.getName)

  import spark.implicits._

  // read the data
  val df = spark.read
    .option("header", "true")
    .option("inferSchema", true)
    .option("charset", "UTF-8")
    .option("delimiter", ",")
    .csv(c.input)

  // load the Hive functions
  HiveUtils.createOrReplaceFunction("ST_Point", "com.esri.hadoop.hive.ST_Point", spark = spark)
  HiveUtils.createOrReplaceFunction("ST_Bin", "com.esri.hadoop.hive.ST_Bin", spark = spark)
  HiveUtils.createOrReplaceFunction("ST_BinEnvelope", "com.esri.hadoop.hive.ST_BinEnvelope", spark = spark)
  HiveUtils.createOrReplaceFunction("ST_AsText", "com.esri.hadoop.hive.ST_AsText", spark = spark)

  // check for null values in the spacial columns of the data
  println(df.filter('dropoff_latitude isNull).count)
  // treat it
  val nonNullPoints = df.na.fill(Map("dropoff_latitude" -> 0, "dropoff_longitude" -> 0))

  // create a table
  nonNullPoints.createOrReplaceTempView("taxi_demo")

  // try the ESRI udfs
  nonNullPoints.withColumn("point", expr("ST_Point(dropoff_longitude,dropoff_latitude)"))
    .select('dropoff_longitude, 'dropoff_latitude, 'point)
    .show

  // run ESRI's sample query
  val esriBinAggregationQuery = spark.sql(
    """
      |FROM (SELECT ST_Bin(0.001, ST_Point(dropoff_longitude,dropoff_latitude)) bin_id, *FROM taxi_demo) bins
      |SELECT ST_BinEnvelope(0.001, bin_id) shape,
      |COUNT(*) count
      |GROUP BY bin_id
    """.stripMargin)
  /**
   * Their query creates a Point from the coordinates, bins the points to a grid using: https://github.com/Esri/spatial-framework-for-hadoop/blob/1ed3d421eb5c062a5309f6c610cd646210daf889/hive/src/main/java/com/esri/hadoop/hive/BinUtils.java
   * AND creates a circle (buffer) around each point of a specified size.
   */
  esriBinAggregationQuery.printSchema
  esriBinAggregationQuery.show

  //  #################################
  // reformulate into sparks SQL DSL - I prefer this notation and it is a bit more typesafe
  val aggregated = nonNullPoints.withColumn("bin_id", expr("ST_Bin(0.001, ST_Point(dropoff_longitude,dropoff_latitude))"))
    .groupBy('bin_id).agg(count("*") as "count")
    .withColumn("shape", expr("ST_BinEnvelope(0.001, bin_id)")) // create the area
    .drop("bin_id")
  aggregated
    .show
  //  #################################
  // run ESRI's hive code
  spark.sql(
    """
      |CREATE TABLE taxi_agg(area BINARY, count DOUBLE)
      |ROW FORMAT SERDE 'com.esri.hadoop.hive.serde.EsriJsonSerDe'
      |STORED AS INPUTFORMAT 'com.esri.json.hadoop.UnenclosedEsriJsonInputFormat'
      |OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
    """.stripMargin)
  spark.sql(
    """
      |FROM (SELECT ST_Bin(0.001, ST_Point(dropoff_longitude,dropoff_latitude)) bin_id, *FROM taxi_demo) bins
      |INSERT OVERWRITE TABLE taxi_agg
      |SELECT ST_BinEnvelope(0.001, bin_id) shape, COUNT(*) count
      |GROUP BY bin_id
    """.stripMargin).show

  //  #################################
  // binary output is not nice to look at, besides that integration of old HIVE functionality (to JSON, partitioning..???)
  // by esri does not seem to play well with spark. Instead, one can simply serialize the WKT representation of the buffer
  val asText = aggregated.withColumn("shape", expr("ST_AsText(shape)"))

  asText.printSchema // note how the shema does not properly match up for the WKT text - it still is of type long ...
  //  asText.show

  // now we are able to store the file in any kind of output format i.e. parquet.
  // for the sake of simplicity - i.e. visualization purposes in QGis I export as a csv file

  // write to hive **the regular way** TODO how to integrate custom serde here? -> not really nice again
  //  esriBinAggregationQuery.write
  //    .mode(SaveMode.Overwrite)
  //    .format("parquet")
  //    .saveAsTable("myAggregation")

  //  #################################
  asText.repartition(1) // want to have a single file
    .write
    .option("header", "true")
    .option("charset", "UTF-8")
    .option("delimiter", ";")
    .csv(c.output)
}
