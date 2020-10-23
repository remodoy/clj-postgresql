(ns clj-postgresql.types.pgobject
  "PGobject parsing magic"
  (:require [clojure.java.jdbc :as jdbc])
  (:import (org.postgresql.util PGobject)))

(defmulti read-pgobject
          "Convert returned PGobject to Clojure value. Dispatch by PGobject type."
          #(keyword (when % (.getType ^PGobject %))))

(defmethod read-pgobject :default
  [^PGobject x]
  (.getValue x))

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [val _ _]
    (read-pgobject val)))
