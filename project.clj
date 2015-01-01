(defproject clj-postgresql "0.5.0"
  :description "PostgreSQL helpers for Clojure projects"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [postgresql "9.3-1102.jdbc41"]
                 [cheshire "5.4.0"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [clj-time "0.9.0"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.slf4j/slf4j-api "1.7.9"]
                 [org.postgis/postgis-jdbc "1.3.3"]
                 [com.taoensso/timbre "3.3.1" :exclusions [org.clojure/clojure]]
                 [prismatic/schema "0.3.3"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [org.slf4j/slf4j-simple "1.7.9"]]
                   :plugins [[lein-midje "3.1.1"]]}})

