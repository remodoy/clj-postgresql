(ns clj-postgresql.types
  "Participate in clojure.java.jdbc's ISQLValue and IResultSetReadColumn protocols
   to allow using PostGIS geometry types without the PGgeometry wrapper, support the
   PGjson type and allow coercing clojure structures into PostGIS types."
  (:require [clj-postgresql.coerce :as coerce] 
            [clj-postgresql.PGjson]
            [clojure.java.jdbc :as jdbc]
            [clojure.xml :as xml]
            [taoensso.timbre :as timbre])
  (:import [org.postgresql.util.PGobject]
           [org.postgis Geometry PGgeometry PGgeometryLW]
           [java.sql PreparedStatement]))

(timbre/refer-timbre)

;;
;; Extend clojure.java.jdbc's protocol for getting SQL values of things to support PostGIS objects.
;;
(extend-protocol jdbc/ISQLValue
  org.postgis.Geometry
  (sql-value [v]
    (PGgeometryLW. v)))

(defn cast-by-meta
  [v]
  (let [m (meta v)]
    (cond (or (:json m) (= (:tag m) clj_postgresql.PGjson)) (clj_postgresql.PGjson. v)
          :else v)))

;;
;; Extend clojure.java.jdbc's protocol for converting query parameters to SQL values.
;; We try to determine which SQL type is correct for which clojure structure.
;; 1. See query parameter meta data. JDBC might already know what PostgreSQL wants.
;; 2. Look into parameter's clojure metadata for type hints
;;

(defn cast-by-parameter-metadata
  [m ^PreparedStatement s ^long i]
  (let [param-meta (.getParameterMetaData s)
        type-name (.getParameterTypeName param-meta i)
        class-name (.getParameterClassName param-meta i)]
    (info "Parameter metadata" type-name class-name)
    (condp = type-name
      "json" (clj_postgresql.PGjson. m)
      :else m)))

(defn map-to-sqlvalue
  [m type]
  (condp = type
    :geometry (jdbc/sql-value (coerce/geojson->postgis m))
    (jdbc/sql-value m)))

(defn vector-to-sqlvalue
  [v type]
  v)

(extend-protocol jdbc/ISQLParameter
  clojure.lang.IPersistentMap
  (set-parameter [m ^PreparedStatement s ^long i]
    (let [meta (.getParameterMetaData s)]
      (if-let [type-name (keyword (.getParameterTypeName meta i))]
        (.setObject s i (map-to-sqlvalue m type-name)) 
        (.setObject s i m)))) 
  clojure.lang.IPersistentVector
  (set-parameter [v ^PreparedStatement s ^long i]
    (let [meta (.getParameterMetaData s)]
      (if-let [type-name (keyword (.getParameterTypeName meta i))]
        (.setObject s i (vector-to-sqlvalue v type-name)) 
        (.setObject s i v)))))

;;
;; Extend clojure.java.jdbc's protocol for interpreting ResultSet column values.
;;
(extend-protocol jdbc/IResultSetReadColumn
  
  ;; Return the PostGIS geometry object instead of PGgeometry wrapper
  org.postgis.PGgeometry
  (result-set-read-column [val _ _]
    (coerce/postgis->geojson (.getGeometry val)))
  
  ;; PGjson already contains a clojure structure.
  ;; Return Clojure map representation of the JSON structure.
  clj_postgresql.PGjson
  (result-set-read-column [val _ _]
    @(.state val))
  
  ;; Parse SQLXML to a Clojure map representing the XML content
  java.sql.SQLXML
  (result-set-read-column [val _ _]
    (xml/parse (.getBinaryStream val)))
  
  ;; Covert java.sql.Array to Clojure vector
  java.sql.Array
  (result-set-read-column [val _ _]
    (into [] (.getArray val))))

 