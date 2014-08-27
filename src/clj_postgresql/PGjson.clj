(ns clj-postgresql.PGjson
  "PGjson objects are used to pass data to PostgreSQL's json fields.
   PGjson uses cheshire to serialize any clojure structure to json.
   JSON data in result sets automatically returned as PGjson objects
   as PGjson is registered as \"json\" datatype handler in
   driverconfig.properties - an extension mechanism in PostgreSQL JDBC."
  (require [cheshire.core :as json])
  (:import org.postgresql.util.PGobject)
  (:gen-class
    :extends org.postgresql.util.PGobject
    :main false
    :state state
    :init init
    :constructors {[] [], [java.lang.Object] []}
    :post-init post-init))

(defn -init
  ([]
    [[] (atom nil)])
  ([obj]
    [[] (atom obj)]))

(defn -post-init
  [this & _]
  (.setType ^PGobject this "json"))

(defn -getValue
  [this]
  (json/generate-string @(.state this)))
  
(defn -setValue
  [this s]
  (reset! (.state this) (json/parse-string s)))

