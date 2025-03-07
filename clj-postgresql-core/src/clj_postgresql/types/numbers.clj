(ns clj-postgresql.types.numbers
  "Convert numbers to SQL parameter values.
  Conversion is done for target types like timestamp for which it makes sense to accept numeric values."
  (:require [clojure.java.jdbc :as jdbc])
  (:import (java.sql Timestamp PreparedStatement)))

(defmulti num->parameter #(keyword %2))

(defmethod num->parameter :timestamptz
  [number _]
  (Timestamp. number))

(defmethod num->parameter :timestamp
  [number _]
  (Timestamp. number))

(defmethod num->parameter :default
  [number _]
  (jdbc/sql-value number))

(extend-protocol clojure.java.jdbc/ISQLParameter
  Number
  (set-parameter [num ^PreparedStatement stmt ^long ix]
    (let [meta (.getParameterMetaData stmt)]
      (if-let [type-name (.getParameterTypeName meta ix)]
        (.setObject stmt ix (num->parameter num type-name))
        (.setObject stmt ix (jdbc/sql-value num))))))
