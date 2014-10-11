(ns clj-postgresql.t-core
  (:use midje.sweet)
  (:require [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as jdbc])
  (:import [java.net InetAddress]))

(defn query
  [& args]
  (jdbc/query (pg/spec) args))

(defn query1
  [& args]
  (let [result (apply query args)]
    (first result)))
      
(fact "Parsing data types works"
      (query1 "SELECT true AS x") => {:x true}
      (query1 "SELECT false AS x") => {:x false}
      (query1 "SELECT false AS x, true AS y") => {:x false :y true}
      (query1 "SELECT '1 2 3'::oidvector AS x") => {:x [1 2 3]}
      (query1 "SELECT '{a,b}'::text[] AS x") => {:x ["a" "b"]}
      (query1 "SELECT '{a,b}'::text[]::anyarray AS x") => {:x ["a" "b"]}
      (query1 "SELECT '{\"foo\":1}'::json AS x") => {:x {"foo" 1}}
      (query1 "SELECT 'CaMeL' AS x") => {:x "CaMeL"})

(fact "Data type parameters work"
      (query1 "SELECT true AS x WHERE true = ?" true) => {:x true}
      (query1 "SELECT true AS x WHERE false = ?" false) => {:x true}
      (query1 "SELECT true AS x WHERE 'a'::text = ?" "a") => {:x true}
      (query1 "SELECT ?::json AS x" {"foo" {"bar" 1}}) => {:x {"foo" {"bar" 1}}}
      (query1 "SELECT ?::json AS x" {:foo {:bar 1}}) => {:x {"foo" {"bar" 1}}}
      (query1 "SELECT ?::int[] AS x" [1 2 7 6 5]) => {:x [1 2 7 6 5]}
      (query1 "SELECT ?::text[] AS x" '("a" "b" "c" "d" "e")) => {:x ["a" "b" "c" "d" "e"]}
      (query1 "SELECT ?::varchar[] AS x" ["a" 1 "B" 2.0]) => {:x ["a" "1" "B" "2.0"]}
      (query1 "SELECT ?::inet AS x" (InetAddress/getByName "127.0.0.1")) => {:x "127.0.0.1"}
      (query1 "SELECT ?::inet AS x" (InetAddress/getByName "::1")) => {:x "::1"})
