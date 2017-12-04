// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app

import at.geoheil.utils.SparkBaseRunner
import org.apache.spark.storage.StorageLevel

import scala.language.postfixOps

object SparkJob extends SparkBaseRunner {
  val spark = createSparkSession(this.getClass.getName)

  // read the data
  //  val df = spark.read
  //    .option("header", "true")
  //    .option("inferSchema", true)
  //    .option("charset", "UTF-8")
  //    .option("delimiter", ",")
  //    .csv(c.input)
  // TODO check if coordinates should be inverted
  //  val pointsRaw = df.select('dropoff_longitude, 'dropoff_latitude).as[PointModel] // assuming x,y
  //  pointsRaw.rdd.mapPartitions()
  val objectRDD = new PointRDD(spark, c.input, 0, FileDataSplitter.CSV, true, StorageLevel.MEMORY_ONLY())

  //  points.rdd.mapPartitions(PostMapperService.map)

}
