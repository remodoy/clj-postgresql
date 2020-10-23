(ns clj-postgresql.types
  "Participate in clojure.java.jdbc's ISQLValue and IResultSetReadColumn protocols
   to allow using PostGIS geometry types without the PGgeometry wrapper, support the
   PGjson type and allow coercing clojure structures into PostGIS types.

   clojure.java.jdbc/ISQLParameter protocol's set-parameter is used to set statement parameters.
   Per default it just delegates to clojure.java.jdbc/ISQLValue protocol's sql-value method.
   Thus if we have a special Clojure/Java type like org.postgis.Geometry, we can just implement
   ISQLValue for that type. But if we want to convert generic maps, vectors, etc. to special database
   types we need to implement ISQLParameter for the generic type and peek into statement's metadata
   to figure out what the target type in database is.

   For parameter handling we implement:
   - map->parameter (IPersistentMap)
   - vec->parameter (IPersistentVector, Sequable)
   - num->parameter (Number)

   Extend clojure.java.jdbc's protocol for converting query parameters to SQL values.
   We try to determine which SQL type is correct for which clojure structure.
   1. See query parameter meta data. JDBC might already know what PostgreSQL wants.
   2. Look into parameter's clojure metadata for type hints
   "
  (:require [clj-postgresql.types maps vectors numbers json inet])
  (:import (org.postgresql.util PGobject PGInterval PGmoney)))


(defn object
  "Make a custom PGobject, e.g. (pg/object \"json\" \"{}\")"
  [type value]
  (doto (PGobject.)
    (.setType (name type))
    (.setValue (str value))))

(defn interval
  "Create a PGinterval. (pg/interval :hours 2)"
  [& {:keys [years months days hours minutes seconds]
      :or {years 0 months 0 days 0 hours 0 minutes 0 seconds 0.0}}]
  (PGInterval. years months days hours minutes ^double seconds))

(defn money
  "Create PGmoney object"
  [amount]
  (PGmoney. ^double amount))
