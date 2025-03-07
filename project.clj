(defproject clj-postgresql "1.0.1-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects"
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :plugins [[lein-sub "0.3.0"]]
  :dependencies [[clj-postgresql/clj-postgresql-core "1.0.1-SNAPSHOT"]
                 [clj-postgresql/clj-postgresql-pool "1.0.1-SNAPSHOT"]
                 [clj-postgresql/clj-postgresql-gis "1.0.1-SNAPSHOT"]
                 [clj-postgresql/clj-postgresql-aws "1.0.1-SNAPSHOT"]]
  :sub ["clj-postgresql-core"
        "clj-postgresql-pool"
        "clj-postgresql-gis"
        "clj-postgresql-aws"])

