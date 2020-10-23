(defproject clj-postgresql/clj-postgresql-core "1.0.0-SNAPSHOT"
  :description "PostgreSQL helpers for Clojure projects."
  :url "https://github.com/remodoy/clj-postgresql"
  :license {:name "Two clause BSD license"
            :url "http://github.com/remodoy/clj-postgresql/README.md"}
  :scm {:dir ".."}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.postgresql/postgresql "42.2.18"]
                 [cheshire "5.10.0"]
                 [org.clojure/java.jdbc "0.7.11"]]
  :repl-options {:init-ns clj-postgresql.core})
