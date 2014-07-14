(defproject clj-postgresql "0.1.0-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [postgresql "9.3-1101.jdbc41"]
                 [cheshire "5.3.1"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [clj-time "0.7.0"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.3.4"]
                 #_[clj-bonecp-url "0.1.1"]
                 [org.slf4j/slf4j-simple "1.7.7"]
                 [org.postgis/postgis-jdbc "1.3.3"]])
