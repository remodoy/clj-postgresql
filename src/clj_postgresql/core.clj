(ns clj-postgresql.core
  (:require [cheshire.core :as json]
            [clojure.xml :as xml]
            [clj-postgresql.pool :refer [pooled-db close-pooled-db!]]
            [clj-postgresql.pgpass :as pgpass])
  (:import org.postgresql.util.PGobject
           org.postgresql.util.PGmoney
           org.postgresql.util.PGInterval))

(defn pg-spec
  "Create database spec for PostgreSQL. Uses PG* environment variables by default
and acceps options in the form:
(pg-spec :dbname ... :host ... :port ... :user ... :password ...)"
  [& {:keys [dbname host port user password] :as opts}]
  (let [env (System/getenv)
        default-user (System/getProperty "user.name")
        extra-opts (dissoc opts :dbname :host :port :user :password)
        db-spec {:dbtype "postgresql"
                 :dbname (or dbname (get env "PGDATABASE") default-user)
                 :host (or host (get env "PGHOST") "localhost")
                 :port (or port (get env "PGPORT") "5432")
                 :user (or user (get env "PGUSER") default-user)}
        password (or password (pgpass/pgpass-lookup db-spec))]
    (cond-> (merge extra-opts db-spec)
            password (assoc :password password))))

(defn pg-pool
  [& rest]
  (let [spec (apply pg-spec rest)]
    (pooled-db spec {})))

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

