(ns clj-postgresql.t-core
  (:use midje.sweet)
  (:require [clj-postgresql.core :as core]))


(fact "The test runner works"
      (not nil) => true)
