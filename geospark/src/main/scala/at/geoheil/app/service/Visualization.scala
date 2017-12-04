//// Copyright (C) 2017-2018 geoHeil
//package at.geoheil.app.service
//
//object Visualization {
//
//  def buildScatterPlot(outputPath: String): Boolean = {
//    val spatialRDD = new PolygonRDD(sparkContext, PolygonInputLocation, PolygonSplitter, false, PolygonNumPartitions)
//    var visualizationOperator = new ScatterPlot(1000, 600, USMainLandBoundary, false)
//    visualizationOperator.CustomizeColor(255, 255, 255, 255, Color.GREEN, true)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    var imageGenerator = new ImageGenerator
//    imageGenerator.SaveRasterImageAsLocalFile(visualizationOperator.rasterImage, outputPath, ImageType.PNG)
//    visualizationOperator = new ScatterPlot(1000, 600, USMainLandBoundary, false, -1, -1, false, true)
//    visualizationOperator.CustomizeColor(255, 255, 255, 255, Color.GREEN, true)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    imageGenerator = new ImageGenerator
//    imageGenerator.SaveVectorImageAsLocalFile(visualizationOperator.vectorImage, outputPath, ImageType.SVG)
//    visualizationOperator = new ScatterPlot(1000, 600, USMainLandBoundary, false, -1, -1, true, true)
//    visualizationOperator.CustomizeColor(255, 255, 255, 255, Color.GREEN, true)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    imageGenerator = new ImageGenerator
//    imageGenerator.SaveVectorImageAsLocalFile(visualizationOperator.distributedVectorImage, outputPath + "-distributed", ImageType.SVG)
//    true
//  }
//
//  /**
//    * Builds the heat map.
//    *
//    * @param outputPath the output path
//    * @return true, if successful
//    */
//  def buildHeatMap(outputPath: String): Boolean = {
//    val spatialRDD = new RectangleRDD(sparkContext, RectangleInputLocation, RectangleSplitter, false, RectangleNumPartitions, StorageLevel.MEMORY_ONLY)
//    val visualizationOperator = new HeatMap(1000, 600, USMainLandBoundary, false, 2)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    val imageGenerator = new ImageGenerator
//    imageGenerator.SaveRasterImageAsLocalFile(visualizationOperator.rasterImage, outputPath, ImageType.PNG)
//    true
//  }
//
//  /**
//    * Builds the choropleth map.
//    *
//    * @param outputPath the output path
//    * @return true, if successful
//    */
//  def buildChoroplethMap(outputPath: String): Boolean = {
//    val spatialRDD = new PointRDD(sparkContext, PointInputLocation, PointOffset, PointSplitter, false, PointNumPartitions, StorageLevel.MEMORY_ONLY)
//    val queryRDD = new PolygonRDD(sparkContext, PolygonInputLocation, PolygonSplitter, false, PolygonNumPartitions, StorageLevel.MEMORY_ONLY)
//    spatialRDD.spatialPartitioning(GridType.RTREE)
//    queryRDD.spatialPartitioning(spatialRDD.grids)
//    spatialRDD.buildIndex(IndexType.RTREE, true)
//    val joinResult = JoinQuery.SpatialJoinQueryCountByKey(spatialRDD, queryRDD, true, false)
//    val visualizationOperator = new ChoroplethMap(1000, 600, USMainLandBoundary, false)
//    visualizationOperator.CustomizeColor(255, 255, 255, 255, Color.RED, true)
//    visualizationOperator.Visualize(sparkContext, joinResult)
//    val frontImage = new ScatterPlot(1000, 600, USMainLandBoundary, false)
//    frontImage.CustomizeColor(0, 0, 0, 255, Color.GREEN, true)
//    frontImage.Visualize(sparkContext, queryRDD)
//    val overlayOperator = new RasterOverlayOperator(visualizationOperator.rasterImage)
//    overlayOperator.JoinImage(frontImage.rasterImage)
//    val imageGenerator = new ImageGenerator
//    imageGenerator.SaveRasterImageAsLocalFile(overlayOperator.backRasterImage, outputPath, ImageType.PNG)
//    true
//  }
//
//  /**
//    * Parallel filter render stitch.
//    *
//    * @param outputPath the output path
//    * @return true, if successful
//    */
//  def parallelFilterRenderStitch(outputPath: String): Boolean = {
//    val spatialRDD = new RectangleRDD(sparkContext, RectangleInputLocation, RectangleSplitter, false, RectangleNumPartitions, StorageLevel.MEMORY_ONLY)
//    val visualizationOperator = new HeatMap(1000, 600, USMainLandBoundary, false, 2, 4, 4, true, true)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    val imageGenerator = new ImageGenerator
//    imageGenerator.SaveRasterImageAsLocalFile(visualizationOperator.distributedRasterImage, outputPath, ImageType.PNG)
//    true
//  }
//
//  /**
//    * Parallel filter render no stitch.
//    *
//    * @param outputPath the output path
//    * @return true, if successful
//    */
//  def parallelFilterRenderNoStitch(outputPath: String): Boolean = {
//    val spatialRDD = new RectangleRDD(sparkContext, RectangleInputLocation, RectangleSplitter, false, RectangleNumPartitions, StorageLevel.MEMORY_ONLY)
//    val visualizationOperator = new HeatMap(1000, 600, USMainLandBoundary, false, 2, 4, 4, true, true)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    val imageGenerator = new ImageGenerator
//    imageGenerator.SaveRasterImageAsLocalFile(visualizationOperator.distributedRasterImage, outputPath, ImageType.PNG)
//    true
//  }
//
//  def earthdataVisualization(outputPath: String): Boolean = {
//    val earthdataHDFPoint = new EarthdataHDFPointMapper(HDFIncrement, HDFOffset, HDFRootGroupName,
//      HDFDataVariableList, HDFDataVariableName, HDFswitchXY, urlPrefix)
//    val spatialRDD = new PointRDD(sparkContext, earthdataInputLocation, earthdataNumPartitions, earthdataHDFPoint, StorageLevel.MEMORY_ONLY)
//    val visualizationOperator = new ScatterPlot(1000, 600, spatialRDD.boundaryEnvelope, ColorizeOption.EARTHOBSERVATION, false, false)
//    visualizationOperator.CustomizeColor(255, 255, 255, 255, Color.BLUE, true)
//    visualizationOperator.Visualize(sparkContext, spatialRDD)
//    val imageGenerator = new ImageGenerator
//    imageGenerator.SaveRasterImageAsLocalFile(visualizationOperator.rasterImage, outputPath, ImageType.PNG)
//    true
//  }
//
//}
