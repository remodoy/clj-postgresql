(ns user
  (:require [clj-postgresql.core :as pg]
            [clj-postgresql.spatial :as st]
            [clj-postgresql.geojson :as gj]
            [clj-postgresql.coerce :as coerce]
            [clojure.java.jdbc :as jdbc]))

(def spec (delay (pg/spec)))
(def pool (delay (pg/pool)))


