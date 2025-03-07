(defproject clj-postgresql/clj-postgresql-core "1.0.1-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects."
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :scm {:dir ".."}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.postgresql/postgresql "42.7.5"]
                 [cheshire "5.13.0"]
                 [org.clojure/java.jdbc "0.7.11"]]
  :repl-options {:init-ns clj-postgresql.core})
