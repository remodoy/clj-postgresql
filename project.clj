(defproject clj-postgresql "0.1.0-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [postgresql "9.3-1101.jdbc4"]
                 [cheshire "5.3.1"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [clj-time "0.6.0"]
                 [org.clojure/java.data "0.1.1"]])
