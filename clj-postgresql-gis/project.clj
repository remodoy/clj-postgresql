(defproject clj-postgresql/clj-postgresql-gis "1.0.0-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects. The PostGIS-parts."
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-postgresql/clj-postgresql-core "1.0.0-SNAPSHOT"]
                 [net.postgis/postgis-jdbc "2.5.0" :exclusions [postgresql org.postgresql/postgresql]]
                 [prismatic/schema "1.1.12"]]
  :repl-options {:init-ns clj-postgresql.spatial})
