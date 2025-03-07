(ns clj-postgresql.types.xml
  "PostgreSQL XML fields will be returned as SQLXML objects. Parse these to clojure.xml maps."
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.xml :as xml]
            [clj-postgresql.types.maps :refer [map->parameter]])
  (:import (java.sql SQLXML)
           (org.postgresql.util PGobject)))

;;
;; Extend clojure.jdbc's protocols to parse XML fields into clojure xml maps.
;;
(extend-protocol jdbc/IResultSetReadColumn
  ;; Parse SQLXML to a Clojure map representing the XML content.
  SQLXML
  (result-set-read-column [val _ _]
    (xml/parse (.getBinaryStream val))))

;;
;; Handle a Clojure map parameter destined to XML field in database.
;; Convert into PGobject.
;;
(defmethod map->parameter :xml
  [m _]
  (doto (PGobject.)
    (.setType "xml")
    (.setValue (with-out-str
                 (xml/emit m)))))
