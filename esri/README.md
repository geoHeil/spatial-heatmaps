# heatmaps for big spatial data with ESRI
Following along with https://github.com/Esri/gis-tools-for-hadoop/wiki/Aggregating-CSV-Data-%28Spatial-Binning%29
I demonstrate how to create spatial heat-maps with ESRIs tools and open source tools like qgis

## dependencies
We need the esri hadoop tools from https://github.com/Esri/spatial-framework-for-hadoop

Execute the following commands. Requires:
- java8 / jdk
- maven
- git
```
git clone https://github.com/Esri/spatial-framework-for-hadoop
cd spatial-framework-for-hadoop
git checkout c50c02d8d94f99b77df34aa3d57498aa8e23571b
mvn clean install
```

if you are on a mac and see a test failure like https://github.com/Esri/spatial-framework-for-hadoop/issues/141:
```
 expected:<1974-08-3[1]> but was:<1974-08-3[0]>
```

simply install with:
```
mvn clean install -DskipTests=True
```

## notes regarding spark
mini project to show how hive sql can easily be executed on spark

use `sbt console`to interactively run queries

or `./sync.sh` to run assembly

or `sbt run` but make sure to set `$SBT_OPTS -Xmx8G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xss2M`
as spark will be launched inside sbt 

for development (in the sbt shell) `~reStart`

also `sbt test` is useful ;)