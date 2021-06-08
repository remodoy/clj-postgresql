(ns clj-postgresql.core
  "Allow using PostgreSQL from Clojure as effortlessly as possible by reading connection parameter defaults from
  PostgreSQL environment variables PGDATABASE, PGHOST, PGPORT, PGUSER and by reading password from ~/.pgpass if available."
  (:require [clj-postgresql.pgpass :as pgpass]
            [clojure.java.jdbc :as jdbc]))

(defn- getenv->map
  "Convert crazy non-map thingy which comes from (System/getenv) into a keywordized map.
  If no argument given, fetch env with (System/getenv)."
  ([x]
   {:post [(map? %)]}
   (zipmap
     (map keyword (keys x))
     (vals x)))
  ([]
   (getenv->map (System/getenv))))

(defn default-spec
  "Reasonable defaults as with the psql command line tool.
  Use username for user and db. Don't use host."
  []
  (let [username (java.lang.System/getProperty "user.name")]
    {:dbtype "postgresql"
     :user   username
     :dbname username}))

(defn env-spec
  "Get db spec by reading PG* variables from the environment."
  [{:keys [PGDATABASE PGHOST PGPORT PGUSER PGPASS] :as env}]
  {:pre  [(map? env)]
   :post [(map? %)]}
  (cond-> {}
          PGDATABASE (assoc :dbname PGDATABASE)
          PGHOST (assoc :host PGHOST)
          PGPORT (assoc :port PGPORT)
          PGUSER (assoc :user PGUSER)
          PGPASS (assoc :password PGPASS)))

(defn spec
  "Create database spec for PostgreSQL. Uses PG* environment variables by default
  and acceps options in the form:
  (spec {:dbname ... :host ... :port ... :user ... :password ...})"
  ([opts]
   {:post [(contains? % :dbname)
           (contains? % :user)]}
   (let [default-spec-opts (default-spec)
         env-spec-opts (env-spec (getenv->map (System/getenv)))
         spec-opts (select-keys opts [:dbname :host :port :user :password])
         extra-opts (dissoc opts :dbname :host :port :user :password)
         db-spec (merge default-spec-opts env-spec-opts spec-opts)
         password (when-not (:password db-spec)
                    (pgpass/pgpass-lookup db-spec))]
     (cond-> (merge extra-opts db-spec)
             password (assoc :password password))))
  ([]
   (spec {})))

(defn close!
  "Close db-spec if possible. Return true if the datasource was closeable and closed."
  [{:keys [datasource]}]
  (when (instance? java.io.Closeable datasource)
    (.close ^java.io.Closeable datasource)
    true))

(defn tables
  [db]
  (jdbc/with-db-metadata [md db]
                         (->> (doall (jdbc/metadata-result (.getTables md nil nil nil (into-array ["TABLE"]))))
                              (map :table_name)
                              (map keyword)
                              (set))))
