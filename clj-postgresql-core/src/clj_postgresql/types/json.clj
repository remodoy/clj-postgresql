(ns clj-postgresql.types.json
  "Extensions to transparently convert between clojure maps and PostgreSQL JSON fields."
  (:require [clj-postgresql.types.maps :refer [map->parameter]]
            [clj-postgresql.types.vectors :refer [vec->parameter]]
            [clj-postgresql.types.pgobject :refer [read-pgobject]]
            [cheshire.core :as json])
  (:import (org.postgresql.util PGobject)))

;;
;; Parameters to PostgreSQL
;;

(def ^:dynamic *json-opts* {})

(defmethod map->parameter :json
  [m _]
  (doto (PGobject.)
    (.setType "json")
    (.setValue (json/generate-string m *json-opts*))))

(defmethod map->parameter :jsonb
  [m _]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (json/generate-string m *json-opts*))))

(defmethod vec->parameter :json
  [v _]
  (doto (PGobject.)
    (.setType "json")
    (.setValue (json/generate-string v *json-opts*))))

(defmethod vec->parameter :jsonb
  [v _]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (json/generate-string v *json-opts*))))

;;
;; Result columns from PostgreSQL
;;

(defmethod read-pgobject :json
  [^PGobject x]
  (when-let [val (.getValue x)]
    (json/parse-string val)))

(defmethod read-pgobject :jsonb
  [^PGobject x]
  (when-let [val (.getValue x)]
    (json/parse-string val)))