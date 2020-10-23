(ns clj-postgresql.types.inet
  (:require [clj-postgresql.types.vectors :refer [vec->parameter]])
  (:import (java.net InetAddress)
           (org.postgresql.util PGobject)))

;;
;; Convert all InetAddress object to string presentation.
;;
(extend-protocol clojure.java.jdbc/ISQLValue
  InetAddress
  (^PGobject sql-value [inet-addr]
    (doto (PGobject.)
      (.setType "inet")
      (.setValue (.getHostAddress inet-addr)))))

;;
;; Convert vectors like [192 168 0 1] into inet object.
;;
(defmethod vec->parameter :inet
  [v _]
  (if (= (count v) 4)
    (doto (PGobject.)
      (.setType "inet")
      (.setValue (clojure.string/join "." v)))
    v))
