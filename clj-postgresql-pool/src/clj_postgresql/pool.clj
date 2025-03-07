(ns clj-postgresql.pool
  "Hikari based connection pool"
  (:require [clj-postgresql.core :as core]
            [hikari-cp.core :as hikari]))

(defn db-spec->pool-config
  "Converts a db-spec with :host :port :dbname and :user to Hikari pool
  config. Hikari options can be passed with `hikari`. See
  https://github.com/tomekw/hikari-cp#configuration-options for that
  list."
  [{:keys [dbtype host port dbname user password hikari]}]
  (let [host-part (when host (if port (format "%s:%s" host port) host))]
    (cond-> {:jdbc-url (format "jdbc:%s://%s/%s" dbtype (or host-part "") dbname)
             :username user}
            hikari (merge hikari)
            password (assoc :password password))))

(defn pooled-db
  [& {:as opts}]
  (let [original-config (core/spec opts)
        config (db-spec->pool-config original-config)]
    {:datasource (hikari/make-datasource config)}))

(defn close-pooled-db!
  [{:keys [datasource]}]
  (hikari/close-datasource datasource))
