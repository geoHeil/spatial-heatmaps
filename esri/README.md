# heatmaps for big spatial data with ESRI
Following along with https://github.com/Esri/gis-tools-for-hadoop/wiki/Aggregating-CSV-Data-%28Spatial-Binning%29
I demonstrate how to create spatial heat-maps with ESRIs tools and open source tools like qgis


## notes regarding spark
mini project to show how hive sql can easily be executed on spark

use `sbt console`to interactively run queries

or `./sync.sh` to run assembly

or `sbt run` but make sure to set `$SBT_OPTS -Xmx8G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xss2M`
as spark will be launched inside sbt 

for development (in the sbt shell) `~reStart`

also `sbt test` is useful ;)