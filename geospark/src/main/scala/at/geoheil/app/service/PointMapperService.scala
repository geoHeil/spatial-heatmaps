// Copyright (C) 2017-2018 geoHeil
package at.geoheil.app.service

import at.geoheil.app.model.PointModel
import com.vividsolutions.jts.geom.{ Coordinate, GeometryFactory, Point }

object PointMapperService {

  @transient lazy val separator = ";"

  /**
   * map basic types to correct geometry using iterator 2 iterator transformation, as geospark requires an
   * RDD of geometry type.
   *
   * @param iterator of Points
   * @return mapped iterator of mapped geometry objects
   */
  def map(iterator: Iterator[PointModel]): Iterator[Point] = {
    @transient lazy val fact = new GeometryFactory()

    iterator.flatMap(p => {
      val geometry = fact.createPoint(new Coordinate(p.x, p.y))
      //  geometry.setUserData(dataToUserdataString(p))
      List(geometry).iterator
    })
  }
}
