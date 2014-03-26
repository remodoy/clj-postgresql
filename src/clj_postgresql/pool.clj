(ns clj-postgresql.pool
  "BoneCP based connection pool"
  (:require [clojure.java.data :as data])
  (:import com.jolbox.bonecp.BoneCPDataSource))

(defn datasource
  "(pool/datasource {:jdbcUrl \"jdbc:postgresql://localhost/test\" :username \"\" :password \"bar\"})"
  [config]
  {:datasource (data/to-java BoneCPDataSource config)})

(defn close-datasource!
  [{:keys [datasource]}]
  (.close ^BoneCPDataSource datasource))
