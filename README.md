# clj-postgresql

A Clojure library designed to help using more advanced PostgreSQL
features in Clojure projects.

- Make use of PGHOST, PGUSER and PGDATABASE when available
- Can use ~/.pgpass for passwords
- Implement clojure.java.jdbc's *ISQLValue* and *IResultSetReadColumn* to coerce clojure types

## Releases
[![Continuous Integration status](https://secure.travis-ci.org/remodoy/clj-postgresql.png)](http://travis-ci.org/remodoy/clj-postgresql)

[![Clojars Project](http://clojars.org/clj-postgresql/clj-postgresql/latest-version.svg)](http://clojars.org/clj-postgresql/clj-postgresql)

Add the following to the `:dependencies` section of your `project.clj` file:

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clj
[clj-postgresql "1.0.0-SNAPSHOT"]
```

[Maven](http://maven.apache.org/) dependency information:

```clj
<dependency>
  <groupId>clj-postgresql</groupId>
  <artifactId>clj-postgresql</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```



## Documentation

### Connecting to database

The pg/spec and pg/pool functions use **PGHOST**, **PGPORT**, **PGUSER** and **PGDATABASE** environment variables
and the *~/.pgpass* file by default. The function arguments can be used to override the connection
parameters in the environment. E.g.:

```clj
(require '[clj-postgresql.core :as pg])
(require '[clojure.java.jdbc :as jdbc])

(def db (pg/spec))
(jdbc/query db ["SELECT true"])

(def pooled-db (pg/pool))
(jdbc/query pooled-db ["SELECT 'hello from db'"])

(def db2 (pg/pool :host "db1.example.com" :user "myaccount" :dbname "anotherdb" :password "foobar"))
(jdbc/query db2 ["SELECT 'test'"])
```

You can pass
[hikari-specific](https://github.com/tomekw/hikari-cp#configuration-options)
options via the `hikari` keyword:

```clj
(def db2 (pg/pool :host "db1.example.com"
                  :user "myaccount"
                  :dbname "anotherdb"
                  :password "foobar"
                  :hikari {:read-only true}))
```

The pool can be closed with:

```clj
(pg/close! db)
```


ACTUALLY DO THIS (prevent compile time resolution of connection params and initialization of the pool):

```clj
(def db (delay (pg/pool)))
(jdbc/query @db ["SELECT 1"])
```



Under the hood, `pg/spec` uses the following logic:

1. Default `:dbtype` is "postgresql". Current username is used for `:dbname` and `:user` as with psql command.
2. PGHOST, PGPORT, PGUSER and PGDATABASE environment variables override default `:host`, `:port`, `:user` and `:dbname`.
3. `pg/spec` function arguments override params.
4. If there is no `:password`, a ~/.pgpass lookup is made.



## Automatic type conversions

With clj-postgresql, clojure.java.jdbc is extended to accept native clojure maps, vectors and sequences as parameter values.
Conversion from clojure type to native SQL type is done based on the parameter type information returned by PostgreSQL.

```clj
(require '[clj-postgresql :as pg])
(require '[clojure.java.jdbc :as jdbc])
(def db (pg/spec))
(jdbc/query db ["SELECT ?::int[] AS arr", [1 2 3 4]])
; => ({:arr [1 2 3 4]})
(jdbc/query db ["SELECT ?::json AS jsonobj" {"foo" "bar"}])
; => ({:jsonobj {"foo" "bar"}})
(jdbc/query db ["SELECT ?::timestamptz AS epoch" 1])
; => ({:epoch #inst "1970-01-01T00:00:00.001000000-00:00"})
```

### Clojure maps

- `json` type parameters accept any Clojure maps
- `geometry` columns accept GeoJSON-like Clojure maps
- `hstore` works as before
- Extendable multimethod to convert map to custom PostgreSQL types e.g. `(defmethod map->parameter :mytype [m _] ...)`.

### Clojure vectors

- PostgreSQL array types like `int[]`, and `text[]` (internally `_int`, `_text`, ...) accept clojure vectors as arguments.
- `inet` type also accepts address as `[192 168 1 11]`
- Extendable multimethod to convert vector to custom PostgreSQL types e.g. `(defmethod vec->parameter :mytype [v _] ...)`.

### Sequables (e.g. lists)

- Are converted to vectors

## Numbers

- Numeric values to `timestampt` and `timestamptz` columns are converted to `java.sql.Timestamp`.
- Extendable multimethod to convert numeric values to custom PostgreSQL types e.g. `(defmethod num->parameter :mytype [num _] ...)`.



## PostGIS types

The `org.postgis.Point`, etc. are of `org.postgis.Geometry` type. They cannot be directly used as query parameters without first wrapping them to `PGgeometry`.
This library extends clojure.java.jdbc to automatically convert Geometry objects into PGgeometry when inserting and automatically convert PGgeometries to specific Geometry objects when reading from database.


```clj
(require '[clj-postgresql.spatial :as st])

(st/point 1 2)
;=> #<Point POINT(1 2)>
(st/point 1 2 3)
;=> #<Point POINT(1 2 3)>
(st/point [1 2])
;=> #<Point POINT(1 2)>

(st/multi-point [[1 2] [3 4] [5 6 7]])
;=> #<MultiPoint MULTIPOINT(1 2,3 4,5 6 7)>

(st/line-string [[1 2] [3 4] [5 6 7]])
;=> #<LineString LINESTRING(1 2,3 4,5 6 7)>

(st/multi-line-string [[[1 2] [3 4]] [[5 6] [7 8]]])
;=> #<MultiLineString MULTILINESTRING((1 2,3 4),(5 6,7 8))>
(st/multi-line-string [[[1 2] [3 4]] (st/line-string [[5 6] [7 8]])])
;=> #<MultiLineString MULTILINESTRING((1 2,3 4),(5 6,7 8))>

(st/linear-ring [[1 2] [3 4]])
;=> #<LinearRing (1 2,3 4)>

(st/polygon [[[1 2] [3 4] [5 6]]])
;=> #<Polygon POLYGON((1 2,3 4,5 6))>
(st/polygon [(st/linear-ring [[1 2] [3 4] [5 6]]) (st/linear-ring [[7 8] [9 10] [11 12]])])
;=> #<Polygon POLYGON((1 2,3 4,5 6),(7 8,9 10,11 12))>
(st/polygon [ [[1 2] [3 4] [5 6]] [[7 8] [9 10] [11 12]] ])
;=> #<Polygon POLYGON((1 2,3 4,5 6),(7 8,9 10,11 12))>

(st/multi-polygon [[[[1 2] [3 4] [5 6]] [[7 8] [9 10] [11 12]]] [[[1 2] [3 4] [5 6]] [[7 8] [9 10] [11 12]]]])
;=> #<MultiPolygon MULTIPOLYGON(((1 2,3 4,5 6),(7 8,9 10,11 12)),((1 2,3 4,5 6),(7 8,9 10,11 12)))>
```



## PostgreSQL geometric types


```clj
(require '[clj-postgresql.core :as pg])

;; point [x y], [pgpoint-or-str]
(pg/point 1 2)
;=> #<PGpoint (1.0,2.0)>
(pg/point [3.0 4])
;=> #<PGpoint (3.0,4.0)>
(pg/point (pg/point 1 2))
;=> #<PGpoint (1.0,2.0)>
(pg/point (PGpoint. 1 2))
;=> #<PGpoint (1.0,2.0)>
(str (pg/point 1 2))
;=> "(1.0,2.0)"

;; box [x1 y1 x2 y2], [p1 p2], [pgbox-or-str]
(pg/box 1 2 3 4)
;=> #<PGbox (1.0,2.0),(3.0,4.0)>
(pg/box (pg/point 1 2) (pg/point 3 4))
;=> #<PGbox (1.0,2.0),(3.0,4.0)>
(pg/box [1 2] [3 4])
;=> #<PGbox (1.0,2.0),(3.0,4.0)>

;; circle [x y r], [point r], [pgcircle-or-str]
(pg/circle 25.0 30.0 5)
;=> #<PGcircle <(25.0,30.0),5.0>>
(pg/circle [25 30] 5)
;=> #<PGcircle <(25.0,30.0),5.0>>
(pg/circle (pg/point 25 30) 5)
;=> #<PGcircle <(25.0,30.0),5.0>>

;; line [x1 y1 x2 y2], [p1 p2], [pgline-or-str]
(pg/line 1 2 3 4)
;=> #<PGline [(1.0,2.0),(3.0,4.0)]>
(pg/line [1 2] [3 4])
;=> #<PGline [(1.0,2.0),(3.0,4.0)]>
(pg/line (pg/point 1 2) (pg/point 3 4))
;=> #<PGline [(1.0,2.0),(3.0,4.0)]>

;; lseg [x1 y1 x2 y2], [p1 p2], [pglseg-or-str]
(pg/lseg 1 2 10 20)
;=> #<PGlseg [(1.0,2.0),(10.0,20.0)]>
(pg/lseg [1 2] [10 20])
;=> #<PGlseg [(1.0,2.0),(10.0,20.0)]>
(pg/lseg (pg/point 1 2) (pg/point 10 20))
;=> #<PGlseg [(1.0,2.0),(10.0,20.0)]>

;; path [points-coll open?]
(pg/path [[1 2] [10 20] [50 100]] true)
;=> #<PGpath [(1.0,2.0),(10.0,20.0),(50.0,100.0)]>
(pg/path [(pg/point 1 2) (pg/point 10 20) (pg/point 50 100)] true)
;=> #<PGpath [(1.0,2.0),(10.0,20.0),(50.0,100.0)]>
(pg/path [[1 2] [3 4] [5 6]] false) ; closed path
;=> #<PGpath ((1.0,2.0),(3.0,4.0),(5.0,6.0))>

;; polygon [points-or-pgpolygon-or-str]
(pg/polygon [[1 2] [3 4] [5 6]])
;=> #<PGpolygon ((1.0,2.0),(3.0,4.0),(5.0,6.0))>
(pg/polygon "((1.0,2.0),(3.0,4.0),(5.0,6.0))")
;=> #<PGpolygon ((1.0,2.0),(3.0,4.0),(5.0,6.0))>

```


# License

Copyright © 2014, Remod Oy
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

