(ns clj-postgresql.types.maps
  (:require [clojure.java.jdbc :as jdbc])
  (:import (clojure.lang IPersistentMap)
           (java.sql PreparedStatement)))

(defmulti map->parameter #(keyword %2))

(defmethod map->parameter :default
  [m _]
  (jdbc/sql-value m))

(extend-protocol jdbc/ISQLParameter
  IPersistentMap
  (set-parameter [m ^PreparedStatement stmt ^long ix]
    (let [meta (.getParameterMetaData stmt)]
      (if-let [type-name (keyword (.getParameterTypeName meta ix))]
        (.setObject stmt ix (map->parameter m type-name))
        (.setObject stmt ix (jdbc/sql-value m))))))
