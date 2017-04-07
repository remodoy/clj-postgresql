(defproject clj-postgresql "0.7.0-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.postgresql/postgresql "42.0.0"]
                 [net.postgis/postgis-jdbc "2.1.7.2" :exclusions [postgresql]]
                 [cheshire "5.4.0"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [clj-time "0.9.0"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [prismatic/schema "0.4.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [org.slf4j/slf4j-simple "1.7.10"]]
                   :plugins [[lein-midje "3.1.1"]]}})

