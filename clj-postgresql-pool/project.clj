(defproject clj-postgresql/clj-postgresql-pool "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-postgresql/clj-postgresql-core "1.0.0-SNAPSHOT"]
                 [hikari-cp "2.13.0"]]
  :repl-options {:init-ns clj-postgresql.pool})
