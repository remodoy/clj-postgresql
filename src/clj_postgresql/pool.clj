(ns clj-postgresql.pool
  "Hikari based connection pool"
  (:require [clojure.java.data :as data]
            [hikari-cp.core :as hikari])
  (:import (java.util.concurrent TimeUnit)))

(defn db-spec->pool-config
  "Converts a db-spec with :host :port :dbname and :user to Hikari pool config"
  [{:keys [dbtype host port dbname user password]}]
  (let [host-part (when host (if port (format "%s:%s" host port) host))
        pool-conf {:jdbc-url (format "jdbc:%s://%s/%s" dbtype (or host-part "") dbname)
                   :username user}]
    (if password
      (assoc pool-conf :password password)
      pool-conf)))

(defn pooled-db
  [spec opts]
  (let [config (merge (db-spec->pool-config spec) opts)]
    {:datasource (hikari/make-datasource config)}))

(defn close-pooled-db!
  [{:keys [datasource]}]
  (hikari/close-datasource datasource))
