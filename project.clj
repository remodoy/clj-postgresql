(defproject clj-postgresql "0.7.0"
  :description "PostgreSQL helpers for Clojure projects"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.postgresql/postgresql "42.2.5"]
                 [net.postgis/postgis-jdbc "2.3.0" :exclusions [postgresql org.postgresql/postgresql]]
                 [hikari-cp "2.7.1"]
                 [cheshire "5.8.1"]
                 [clj-time "0.15.1"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [prismatic/schema "1.1.10"]]
  :profiles {:dev {:dependencies [[midje "1.9.8"]]
                   :plugins [[lein-midje "3.1.1"]]}})

