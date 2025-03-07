(ns clj-postgresql.types.vectors
  "Parameters:
  Convert Clojure vectors into SQL arrays if target type is e.g. _int.
  For non array target types, dispatch vec->parameter multimethod for converting
  Clojure vector specific target type. Convert any non vector Sequable parameters to vector.

  Query results:
  Convert array to vector of objects.
  "
  (:require [clojure.java.jdbc :as jdbc]
            [clj-postgresql.types.pgobject :refer [read-pgobject]])
  (:import (clojure.lang IPersistentVector Seqable)
           (java.sql PreparedStatement Array)
           (org.postgresql.util PGobject)))

(defmulti vec->parameter #(keyword %2))

(defmethod vec->parameter :default
  [v _]
  (jdbc/sql-value v))

(extend-protocol jdbc/ISQLParameter
  IPersistentVector
  (set-parameter [v ^PreparedStatement stmt ^long ix]
    (let [conn (.getConnection stmt)
          meta (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta ix)]
      (if-let [elem-type (when type-name (second (re-find #"^_(.*)" type-name)))]
        (.setObject stmt ix (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt ix (vec->parameter v type-name)))))
  Seqable
  (set-parameter [seqable ^PreparedStatement stmt ^long ix]
    (jdbc/set-parameter (vec (seq seqable)) stmt ix)))

;;
;; Handle java.sql.Array results
;;

(extend-protocol jdbc/IResultSetReadColumn
  ;; Covert java.sql.Array to Clojure vector
  Array
  (result-set-read-column [val _ _]
    (vec (.getArray val))))

;;
;; Handle arrays and vectors coming in as PGobject
;;

(defn- read-pg-vector
  "oidvector, int2vector, etc. are space separated lists"
  [s]
  (when (seq s)
    (clojure.string/split s #"\s+")))

(defn- read-pg-array
  "Arrays are of form {1,2,3}"
  [s]
  (when (seq s)
    (when-let [[_ content] (re-matches #"^\{(.+)\}$" s)]
      (if-not (empty? content)
        (clojure.string/split content #"\s*,\s*")
        []))))

(defmethod read-pgobject :oidvector
  [^PGobject x]
  (when-let [val (.getValue x)]
    (mapv read-string (read-pg-vector val))))

(defmethod read-pgobject :int2vector
  [^PGobject x]
  (when-let [val (.getValue x)]
    (mapv read-string (read-pg-vector val))))

(defmethod read-pgobject :anyarray
  [^PGobject x]
  (when-let [val (.getValue x)]
    (vec (read-pg-array val))))
