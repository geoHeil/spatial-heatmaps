# heatmaps for big spatial data with geospark
Geospark babylon provides a nice tool for visualizing large quantities of data.

## dependencies
We need the esri hadoop tools from https://github.com/Esri/spatial-framework-for-hadoop

Requires:
- java8 / jdk
- maven
- git


> WARN: currently does not yet work - fails to render the image due to null pointer exception

Run via the following command - optionally, you can specify some more spark configuration.
```
spark-submit --verbose \
        --class at.geoheil.app.SparkJob \
	target/scala-2.11/sparkMiniSample-assembly-0.1-SNAPSHOT.jar
```
> WARN: currently, this will fail due to shading problems: https://github.com/pureconfig/pureconfig/issues/333

## notes regarding spark
mini project to show how hive sql can easily be executed on spark

use `sbt console`to interactively run queries

or `./sync.sh` to run assembly

or `sbt run` but make sure to set `$SBT_OPTS -Xmx8G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xss2M`
as spark will be launched inside sbt 

for development (in the sbt shell) `~reStart`

also `sbt test` is useful ;)