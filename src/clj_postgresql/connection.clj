(ns clj-postgresql.connection
  (:require [clj-postgresql.pgpass :refer [pgpass-lookup]]))

(defn env->db-spec
  "Use PGHOST, PGPORT, PGDATABASE and PGUSER to determine connection information for db-spec map
(env->db-spec) => {:dbtype \"postgresql\" :dbname $PGDATABASE :host $PGHOST :port $PGPORT :user $PGUSER}"
  [env default-user]
  {:dbtype "postgresql"
   :host (get env "PGHOST" "localhost")
   :port (get env "PGPORT" "5432")
   :dbname (get env "PGDATABASE" default-user)
   :user (get env "PGUSER" default-user)})

(defn db-spec
  []
  (let [db (env->db-spec (System/getenv) (System/getProperty "user.name"))
        password (pgpass-lookup db)]
    (if password
      (assoc db :password password)
      db)))

