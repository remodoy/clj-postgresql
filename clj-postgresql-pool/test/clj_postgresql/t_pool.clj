(ns clj-postgresql.t-pool
  (:require [clojure.test :refer :all]
            [clj-postgresql.pool :as pool]
            [clojure.java.jdbc :as jdbc]))

(defn query
  [& args]
  (jdbc/query (pool/pooled-db) args))

(defn query1
  [& args]
  (let [result (apply query args)]
    (first result)))

(deftest pool-test
  (testing "Pool query works"
    (is (= (jdbc/execute! (pool/pooled-db) "CREATE TEMPORARY TABLE bob(id text)") [0]))
    (is (= (query1 "SELECT true AS x") {:x true}))
    (is (= (try
             (jdbc/execute! (pool/pooled-db :hikari {:read-only true}) "CREATE TEMPORARY TABLE bob(id text)")
             (catch org.postgresql.util.PSQLException e
               (.getMessage e))) "ERROR: cannot execute CREATE TABLE in a read-only transaction"))))
