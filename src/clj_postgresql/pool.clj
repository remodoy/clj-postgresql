(ns clj-postgresql.pool
  "BoneCP based connection pool"
  (:require [clojure.java.data :as data])
  (:import com.jolbox.bonecp.BoneCPDataSource))

(defn db-spec->pool-config
  "Converts a db-spec with :host :port :dbname and :user to bonecp pool config"
  [{:keys [dbtype host port dbname user password]}]
  {:jdbcUrl (format "jdbc:%s://%s:%s/%s" dbtype host port dbname)
   :username user
   :password password})

(defn pooled-db
  [spec opts]
  (let [config (merge (db-spec->pool-config spec) opts)]
    {:datasource (data/to-java BoneCPDataSource config)}))

(defn close-pooled-db!
  [{:keys [datasource]}]
  (.close ^BoneCPDataSource datasource))
