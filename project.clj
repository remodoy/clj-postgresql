(defproject clj-postgresql "0.7.0"
  :description "PostgreSQL helpers for Clojure projects"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.postgresql/postgresql "42.1.4"]
                 [net.postgis/postgis-jdbc "2.2.1" :exclusions [postgresql org.postgresql/postgresql]]
                 [cheshire "5.8.0"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [clj-time "0.14.2"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [prismatic/schema "1.1.7"]]
  :profiles {:dev {:dependencies [[midje "1.9.0"]
                                  [org.slf4j/slf4j-simple "1.7.25"]]
                   :plugins [[lein-midje "3.1.1"]]}})

