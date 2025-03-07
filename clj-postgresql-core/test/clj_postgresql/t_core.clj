(ns clj-postgresql.t-core
  (:require [clojure.test :refer :all]
            [clj-postgresql.core :as pg]
            [clj-postgresql.types]
            [clojure.java.jdbc :as jdbc])
  (:import [java.net InetAddress]))

(defn query
  [& args]
  (jdbc/query (pg/spec) args))

(defn query1
  [& args]
  (let [result (apply query args)]
    (first result)))

(deftest core-test
  (testing "Parsing data types works"
    (is (= (query1 "SELECT true AS x") {:x true}))
    (is (= (query1 "SELECT false AS x") {:x false}))
    (is (= (query1 "SELECT false AS x, true AS y") {:x false :y true}))
    (is (= (query1 "SELECT '1 2 3'::oidvector AS x") {:x [1 2 3]}))
    (is (= (query1 "SELECT '{a,b}'::text[] AS x") {:x ["a" "b"]}))
    (is (= (query1 "SELECT '{a,b}'::text[]::anyarray AS x") {:x ["a" "b"]}))
    (is (= (query1 "SELECT '{\"foo\":1}'::json AS x") {:x {"foo" 1}}))
    (is (= (query1 "SELECT '{\"foo\":1}'::jsonb AS x") {:x {"foo" 1}}))
    (is (= (query1 "SELECT 'CaMeL' AS x") {:x "CaMeL"})))

  (testing "Data type parameters work"
    (is (= (query1 "SELECT true AS x WHERE true = ?" true) {:x true}))
    (is (= (query1 "SELECT true AS x WHERE false = ?" false) {:x true}))
    (is (= (query1 "SELECT true AS x WHERE 'a'::text = ?" "a") {:x true}))
    (is (= (query1 "SELECT ?::json AS x" {"foo" {"bar" 1}}) {:x {"foo" {"bar" 1}}}))
    (is (= (query1 "SELECT ?::json AS x" {:foo {:bar 1}}) {:x {"foo" {"bar" 1}}}))
    (is (= (query1 "SELECT ?::jsonb AS x" {"foo" {"bar" 1}}) {:x {"foo" {"bar" 1}}}))
    (is (= (query1 "SELECT ?::jsonb AS x" {:foo {:bar 1}}) {:x {"foo" {"bar" 1}}}))
    (is (= (query1 "SELECT ?::int[] AS x" [1 2 7 6 5]) {:x [1 2 7 6 5]}))
    (is (= (query1 "SELECT ?::text[] AS x" '("a" "b" "c" "d" "e")) {:x ["a" "b" "c" "d" "e"]}))
    (is (= (query1 "SELECT ?::varchar[] AS x" ["a" 1 "B" 2.0]) {:x ["a" "1" "B" "2.0"]}))
    (is (= (query1 "SELECT ?::inet AS x" (InetAddress/getByName "127.0.0.1")) {:x "127.0.0.1"}))
    (is (= (query1 "SELECT ?::inet AS x" (InetAddress/getByName "::1")) {:x "::1"}))))
