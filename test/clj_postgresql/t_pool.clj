(ns clj-postgresql.t-pool
  (:use midje.sweet)
  (:require [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as jdbc]))

(defn query
  [& args]
  (jdbc/query (pg/pool) args))

(defn query1
  [& args]
  (let [result (apply query args)]
    (first result)))

(fact "Pool query works"
      (jdbc/execute! (pg/pool) "CREATE TEMPORARY TABLE bob(id text)") => [0]
      (query1 "SELECT true AS x") => {:x true}
      (try
        (jdbc/execute! (pg/pool :hikari {:read-only true}) "CREATE TEMPORARY TABLE bob(id text)")
        (catch org.postgresql.util.PSQLException e
          (.getMessage e))) => "ERROR: cannot execute CREATE TABLE in a read-only transaction")
