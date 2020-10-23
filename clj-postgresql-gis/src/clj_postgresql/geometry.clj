(ns clj-postgresql.geometry
  (:require [clojure.java.jdbc :as jdbc]
            [clj-postgresql.types.maps :refer [map->parameter]]
            [clj-postgresql.coerce :as coerce])
  (:import [org.postgis Geometry PGgeometry PGgeometryLW]))

;;;;
;;
;; Data type conversion for SQL query parameters
;;
;;;;

;;
;; Allow using PostsGIS Geometry objects as query parameters.
;; The Geometry will be made PGgeometryLW and passed to the backend with the efficient EWKB format.
;;
(extend-protocol jdbc/ISQLValue
  Geometry
  (sql-value [v]
    (PGgeometryLW. v)))

;;
;; Allow using geojson maps as parameters.
;;
(defmethod map->parameter :geometry
  [m _]
  (jdbc/sql-value (coerce/geojson->postgis m)))

;;
;; Interpret query results.
;; PGgeometry typed results will be converted into geojson maps.
;;
(extend-protocol jdbc/IResultSetReadColumn
  PGgeometry
  (result-set-read-column [val _ _]
    (coerce/postgis->geojson (.getGeometry val))))

