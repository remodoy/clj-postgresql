(ns clj-postgresql.core
  (:require [cheshire.core :as json]
            [clojure.xml :as xml]
            [clj-postgresql.pool :as pool])
  (:import org.postgresql.util.PGobject
           org.postgresql.util.PGmoney
           org.postgresql.util.PGInterval))

(defn pg-object
  [type value]
  (doto (PGobject.)
    (.setType (name type))
    (.setValue (str value))))

(defn pg-interval
  "Create a PG interval"
  [& {:keys [years months days hours minutes seconds]
      :or {years 0 months 0 days 0 hours 0 minutes 0 seconds 0.0}}]
  (PGInterval. years months days hours minutes ^double seconds))

(defn pg-money
  "Create PGmoney object"
  [amount]
  (PGmoney. ^double amount))

(defn pg-json
  "Make PostgreSQL JSON object"
  [m]
  (pg-object :json (json/generate-string m)))

(defn pg-xml
  "Make PostgreSQL XML object"
  [s]
  (pg-object :xml (str s)))

(defn parse-jdbc-xml
  "Parse SQLXML received from JDBC"
  [^java.sql.SQLXML obj]
  (xml/parse (.getString obj)))

