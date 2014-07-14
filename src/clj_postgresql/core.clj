(ns clj-postgresql.core
  "Allow using PostgreSQL from Clojure as effortlessly as possible by reading connection parameter defaults from
PostgreSQL environment variables PGDATABASE, PGHOST, PGPORT, PGUSER and by reading password from ~/.pgpass if available." 
  (:require [cheshire.core :as json]
            [clojure.xml :as xml]
            [clj-postgresql.pool :refer [pooled-db close-pooled-db!] :as pool]
            [clj-postgresql.pgpass :as pgpass]
            [clojure.java.jdbc :as jdbc])
  (:import org.postgresql.util.PGobject
           org.postgresql.util.PGmoney
           org.postgresql.util.PGInterval))

(defn getenv->map
  "Convert crazy non-map thingy which comes from (System/getenv) into a keywordized map.
If no argument given, fetch env with (System/getenv)."
  ([x]
    {:pre [(= (type x) java.util.Collections$UnmodifiableMap)]
     :post [(map? %)]}
    (zipmap
      (map keyword (keys x))
      (vals x)))
  ([]
    (getenv->map (System/getenv))))

(defn env-spec
  "Get db spec by reading PG* variables from the environment."
  [{:keys [PGDATABASE PGHOST PGPORT PGUSER] :as env}]
  {:pre [(map? env)]
   :post [(map? %)]}
  (cond-> {}
          PGDATABASE (assoc :dbname PGDATABASE)
          PGHOST (assoc :host PGHOST)
          PGPORT (assoc :port PGPORT)
          PGUSER (assoc :user PGUSER)))

(defn pg-spec
  "Create database spec for PostgreSQL. Uses PG* environment variables by default
and acceps options in the form:
(pg-spec :dbname ... :host ... :port ... :user ... :password ...)"
  [& {:keys [password] :as opts}]
  {:post [(contains? % :dbname)
          (contains? % :host)
          (contains? % :port)
          (contains? % :user)]}
  (let [spec-opts (select-keys opts [:dbname :host :port :user])
        extra-opts (dissoc opts :dbname :host :port :user :password)
        db-spec (merge {:dbtype "postgresql" :port "5432"}
                       (env-spec (getenv->map (System/getenv)))
                       spec-opts)
        password (or password (pgpass/pgpass-lookup db-spec))]
    (cond-> (merge extra-opts db-spec)
            password (assoc :password password))))

(defn connect-hook
  [conn]
  (doto conn
    (.addDataType "money" ^PGobject PGmoney)))

(defn pg-pool
  [& rest]
  (let [spec (apply pg-spec rest)]
    (pooled-db spec {:connectionHook (pool/make-hook connect-hook)})))

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

(defn json
  "Make PostgreSQL JSON object"
  [m]
  (object :json (json/generate-string m)))

(defn xml
  "Make PostgreSQL XML object"
  [s]
  (object :xml (str s)))

(defn parse-jdbc-xml
  "Parse SQLXML received from JDBC"
  [^java.sql.SQLXML obj]
  (xml/parse (.getString obj)))

(defn tables
  [db]
  (jdbc/with-db-metadata [md db]
    (->> (doall (jdbc/metadata-result (.getTables md nil nil nil (into-array ["TABLE"]))))
      (map :table_name)
      (map keyword)
      (set))))
