// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app

import at.geoheil.utils.SparkBaseRunner
import org.apache.spark.sql.functions._

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

  // create a table
  df.createOrReplaceTempView("taxi_demo")

  // load the Hive functions
  spark.sql("create function ST_Bin as 'com.esri.hadoop.hive.ST_Bin'")
  spark.sql("create function ST_Point as 'com.esri.hadoop.hive.ST_Point'")
  spark.sql("create function ST_BinEnvelope as 'com.esri.hadoop.hive.ST_BinEnvelope'")

  // try them
  df.withColumn("point", expr("ST_Point(dropoff_longitude,dropoff_latitude)"))
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
  esriBinAggregationQuery.printSchema
  esriBinAggregationQuery.show

  //  #################################
  // reformulate into sparks SQL DSL - I prefer this notation and it is a bit more typesafe
  df.withColumn("bin_id", expr("ST_Bin(0.001, ST_Point(dropoff_longitude,dropoff_latitude))"))
    .groupBy('bin_id).agg(count("*"))
    .withColumn("shape", expr("ST_BinEnvelope(0.001, bin_id)"))
    .drop("bin_id")
    .show
  //  #################################

  // run ESRI's hive code

  // TODO why is esris code not found?
  // ClassNotFoundException: Class com.esri.hadoop.hive.serde.JsonSerde not found
  spark.sql(
    """
      |CREATE TABLE taxi_agg(area BINARY, count DOUBLE)
      |ROW FORMAT SERDE 'com.esri.hadoop.hive.serde.JsonSerde'
      |STORED AS INPUTFORMAT 'com.esri.json.hadoop.UnenclosedEsriJsonInputFormat'
      |OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
    """.stripMargin).show
  spark.sql(
    """
      |FROM (SELECT ST_Bin(0.001, ST_Point(dropoff_longitude,dropoff_latitude)) bin_id, *FROM taxi_demo) bins
      |INSERT OVERWRITE TABLE taxi_agg
      |SELECT ST_BinEnvelope(0.001, bin_id) shape, COUNT(*) count
      |GROUP BY bin_id;
    """.stripMargin).show

  //  #################################
  // write to hive **the regular way** TODO how to integrate custom serde here?
  //  esriBinAggregationQuery.write
  //    .mode(SaveMode.Overwrite)
  //    .format("parquet")
  //    .saveAsTable("myAggregation")

  //  #################################
}
