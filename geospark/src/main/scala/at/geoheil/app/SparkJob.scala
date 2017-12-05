// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app

import java.io.FileInputStream
import java.util.Properties

import at.geoheil.app.model.PointModel
import at.geoheil.app.service.{PointMapperService, Vis}
import at.geoheil.utils.SparkBaseRunner
import com.vividsolutions.jts.geom.Envelope
import org.apache.spark.storage.StorageLevel
import org.datasyslab.babylon.core.ImageGenerator
import org.datasyslab.babylon.extension.visualizationEffect.HeatMap
import org.datasyslab.babylon.utils.ImageType
import org.datasyslab.geospark.enums.FileDataSplitter
import org.datasyslab.geospark.spatialRDD.{PointRDD, RectangleRDD}

object SparkJob extends SparkBaseRunner {
  val spark = createSparkSession(this.getClass.getName)
  import spark.implicits._

  // read the data
//  val df = spark.read
//    .option("header", "true")
//    .option("inferSchema", true)
//    .option("charset", "UTF-8")
//    .option("delimiter", ",")
//    .csv(c.input)
//    .csv("../data/trip_data/trip_data_1.csv")
//    .limit(100)
//  println(df.filter('dropoff_latitude isNull).count)
//  // treat it
//  val nonNullPoints = df.na.fill(Map("dropoff_latitude" -> 0, "dropoff_longitude" -> 0))
//  // TODO check if coordinates should be inverted
//  val pointsRaw = nonNullPoints.select('dropoff_longitude as "x", 'dropoff_latitude as "y").as[PointModel] // assuming x,y
//  val pointRDD = pointsRaw.rdd.mapPartitions(PointMapperService.map)
//  val pointGeospark = new PointRDD(pointRDD)
//  val output = c.output
//  val output = "../data/aggregatedResult_heatmap"
//  val output = System.getProperty("user.dir") + "/heatmap"
//
//  Vis.buildHeatMap(output, pointGeospark, spark)
  //  val pointRDD = new PointRDD(spark.sparkContext, c.input, 0, FileDataSplitter.CSV, true, StorageLevel.MEMORY_ONLY)
  //  points.rdd.mapPartitions(PostMapperService.map)

  val resourcePath = "src/test/resources/"
  val ConfFile = new FileInputStream(resourcePath + "babylon.rectangle.properties")
  val prop = new Properties()
  prop.load(ConfFile)
  val RectangleInputLocation = "file://" + System.getProperty("user.dir") + "/" + resourcePath + prop.getProperty("inputLocation")
  val RectangleOffset = prop.getProperty("offset").toInt
  val RectangleSplitter = FileDataSplitter.getFileDataSplitter(prop.getProperty("splitter"))
  val RectangleNumPartitions = prop.getProperty("numPartitions").toInt


  val demoOutputPath = "target/demo"
  val heatMapOutputPath = System.getProperty("user.dir") + "/" + demoOutputPath + "/heatmap"
  buildHeatMap(heatMapOutputPath)
  def buildHeatMap(outputPath: String): Boolean = {
    val spatialRDD = new RectangleRDD(spark.sparkContext, RectangleInputLocation, RectangleSplitter, false, RectangleNumPartitions, StorageLevel.MEMORY_ONLY)
    val USMainLandBoundary = new Envelope(-126.790180, -64.630926, 24.863836, 50.000)
    val visualizationOperator = new HeatMap(1000, 600, USMainLandBoundary, false, 2)
    visualizationOperator.Visualize(spark.sparkContext, spatialRDD)
    val imageGenerator = new ImageGenerator
    imageGenerator.SaveRasterImageAsLocalFile(visualizationOperator.rasterImage, outputPath, ImageType.PNG)
    true
  }

}
