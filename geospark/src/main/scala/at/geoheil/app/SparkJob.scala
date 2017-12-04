// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app

import at.geoheil.app.model.PointModel
import at.geoheil.app.service.{ PointMapperService, Vis }
import at.geoheil.utils.SparkBaseRunner
import org.datasyslab.geospark.spatialRDD.PointRDD

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
    .limit(100)
  println(df.filter('dropoff_latitude isNull).count)
  // treat it
  val nonNullPoints = df.na.fill(Map("dropoff_latitude" -> 0, "dropoff_longitude" -> 0))
  // TODO check if coordinates should be inverted
  val pointsRaw = nonNullPoints.select('dropoff_longitude as "x", 'dropoff_latitude as "y").as[PointModel] // assuming x,y
  val pointRDD = pointsRaw.rdd.mapPartitions(PointMapperService.map)
  val pointGeospark = new PointRDD(pointRDD)

  Vis.buildHeatMap(c.output, pointGeospark, spark)
  //  val pointRDD = new PointRDD(spark.sparkContext, c.input, 0, FileDataSplitter.CSV, true, StorageLevel.MEMORY_ONLY)
  //  points.rdd.mapPartitions(PostMapperService.map)

}
