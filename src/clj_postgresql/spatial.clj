(ns clj-postgresql.spatial
  ":require [clj-postgresql.spatial :as st]"
  (:import [org.postgis Geometry PGgeometryLW PGgeometry LineString LinearRing MultiLineString MultiPoint MultiPolygon Point Polygon]))

(defn point
  "Make a 2D or 3D Point."
  ([x y]
    (Point. x y))
  ([x y z]
    (Point. x y z)))

(defn multi-point
  "Make a MultiPoint from collection of Points."
  [points]
  (MultiPoint. (into-array Point points)))

(defn line-string
  [points]
  (LineString. (into-array Point points)))

(defn multi-line-string
  [line-strings]
  (MultiLineString. (into-array LineString line-strings)))

(defn linear-ring
  "Used for constructing Polygons from Points."
  [points]
  (LinearRing. (into-array Point points)))

(defn set-srid
  [^Geometry x srid]
  (doto x
    (.setSrid ^int srid)))

(defn polygon
  "Make a Polygon from a collection of Points."
  [points]
  (Polygon. (into-array LinearRing [(linear-ring points)])))

(defn multi-polygon
  "Make a MultiPolygon from collection of Polygons."
  [polygons]
  (MultiPolygon. (into-array Polygon polygons)))

(defn pg-geom
  [geometry]
  (PGgeometryLW. geometry))

