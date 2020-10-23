(ns clj-postgresql.types.geometric
  (:import (org.postgresql.geometric PGpoint PGbox PGcircle PGline PGlseg PGpath PGpolygon)))

;;
;; Constructors for geometric Types
;;

(defn point
  "Create a PGpoint object"
  ([x y]
   (PGpoint. x y))
  ([obj]
   (cond
     (instance? PGpoint obj) obj
     (coll? obj) (point (first obj) (second obj))
     :else (PGpoint. (str obj)))))

(defn box
  "Create a PGbox object"
  ([p1 p2]
   (PGbox. (point p1) (point p2)))
  ([x1 y1 x2 y2]
   (PGbox. x1 y1 x2 y2))
  ([obj]
   (if (instance? PGbox obj)
     obj
     (PGbox. (str obj)))))

(defn circle
  "Create a PGcircle object"
  ([x y r]
   (PGcircle. x y r))
  ([center-point r]
   (PGcircle. (point center-point) r))
  ([obj]
   (if (instance? PGcircle obj)
     obj
     (PGcircle. (str obj)))))

(defn line
  "Create a PGline object"
  ([x1 y1 x2 y2]
   (PGline. x1 y1 x2 y2))
  ([p1 p2]
   (PGline. (point p1) (point p2)))
  ([obj]
   (if (instance? PGline obj)
     obj
     (PGline. (str obj)))))

(defn lseg
  "Create a PGlseg object"
  ([x1 y1 x2 y2]
   (PGlseg. x1 y1 x2 y2))
  ([p1 p2]
   (PGlseg. (point p1) (point p2)))
  ([obj]
   (if (instance? PGlseg obj)
     obj
     (PGlseg. (str obj)))))

(defn path
  "Create a PGpath object"
  ([points open?]
   (PGpath. (into-array PGpoint (map point points)) open?))
  ([obj]
   (if (instance? PGpath obj)
     obj
     (PGpath. (str obj)))))

(defn polygon
  "Create a PGpolygon object"
  [points-or-str]
  (if (coll? points-or-str)
    (PGpolygon. ^"[Lorg.postgresql.geometric.PGpoint;" (into-array PGpoint (map point points-or-str)))
    (PGpolygon. ^String (str points-or-str))))
