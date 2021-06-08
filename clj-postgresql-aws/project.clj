(defproject clj-postgresql/clj-postgresql-aws "1.0.0-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects."
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :scm {:dir ".."}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-postgresql/clj-postgresql-core "1.0.0-SNAPSHOT"]
                 [com.amazonaws/aws-java-sdk-rds "1.11.893"]]
  :repl-options {:init-ns clj-postgresql.aws})
