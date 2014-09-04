# clj-postgresql

A Clojure library designed to help using more advanced PostgreSQL
features is Clojure projects.

- Make use of PGHOST, PGUSER and PGDATABASE when available
- Can use ~/.pgpass for passwords
- Implement PGjson type for PostgreSQL JDBC driver
- Implement clojure.java.jdbc's *ISQLValue* and *IResultSetReadColumn* to coerce clojure types
- Type hinting for SQL parameters

## Releases
[![Continuous Integration status](https://secure.travis-ci.org/remodoy/clj-postgresql.png)](http://travis-ci.org/remodoy/clj-postgresql)

[![Clojars Project](http://clojars.org/clj-postgresql/clj-postgresql/latest-version.svg)](http://clojars.org/clj-postgresql/clj-postgresql)

Add the following to the `:dependencies` section of your `project.clj` file:

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```
[clj-postgresql "0.2.0-SNAPSHOT"]
```

[Maven](http://maven.apache.org/) dependency information:

```
<dependency>
  <groupId>clj-postgresql</groupId>
  <artifactId>clj-postgresql</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```



## Documentation

* [API docs](http://remodoy.github.io/clj-postgresql/)



	(ns ...
		(:require ...
			[clj-postgresql.core :as pg]))
	
	(defonce db (pg/spec :host "localhost" :dbname "testdb" :username "myuser" :password "apassword"))
	(jdbc/query db ["SELECT ? AS testcolumn", (pg/json {:foo "bar"})])

	(defonce pool (pg/pool :host "localhost" :dbname "testdb" :username "myuser" :password "apassword"))
	(jdbc/query pool ["SELECT 1"])


### Connecting to database

The pg-spec and pg-pool functions use **PGHOST**, **PGPORT**, **PGUSER** and **PGDATABASE** environment variables
and the *~/.pgpass* file by default. The function arguments can be used to override the connection
parameters in the environment. E.g.:

```
(def db (delay (pg-pool :dbname "anotherdb")))
(jdbc/query @db ["SELECT 'test'"])
```

The pool can be closed with:

```
(close-pooled-db! @db)
```

Under the hood, db-spec uses the following logic:

1. Default `:dbtype` is "postgresql". Current username is used for `:dbname` and `:user` as with psql command.
2. PGHOST, PGPORT, PGUSER and PGDATABASE environment variables override default `:host`, `:port`, `:user` and `:dbname`.
3. `pg-spec` function arguments override params.
4. If there is no `:password`, a ~/.pgpass lookup is made.




## Type hinting SQL parameters

SQL Query parameters can be type hinted to convert to a proper type of PGobject.

```
(jdbc/insert! @db :testtable [:col1 :col2 :col3] ["foo" "bar" ^PGjson {:name "baz"}])

(jdbc/query @db ["SELECT ? AS jsonthingy", ^PGjson {:foo "bar", :baz "quux"}])
;=> ({:jsonthingy {"foo" "bar", "baz" "quux"}})
```

The implemented meta tags are:

- `^PGjson` to wrap a clojure map into a PGjson object
- `^:json` the same

PG type | Clojure structures  | Meta tag
--------|---------------------|---------------------
json    | map                 | `^PGjson`, `^:json`



## PostGIS type

The `org.postgis.Point`, etc. are of `org.postgis.Geometry` type. They cannot be directly used as query parameters without first wrapping them to `PGgeometry`. This library extends clojure.java.jdbc to automatically convert Geometry objects into PGgeometry when inserting and automatically convert PGgeometries to specific Geometry objects when reading from database.

```
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




```
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


## Roadmap

- Travis builds
- API docs
- PostGIS support

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
