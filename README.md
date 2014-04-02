# postgresql

A Clojure library designed to help using more advanced PostgreSQL
features is Clojure projects.

## Usage

Depend on:
    [clj-postgresql "0.1.0-SNAPSHOT"]
(Latest version: https://clojars.org/clj-postgresql)

	(ns ...
		(:require ...
			[clj-postgresql.core :as pg]))
	
	(defonce db (pg/pg-spec :host "localhost" :dbname "testdb" :username "myuser" :password "apassword"))
	(jdbc/query db ["SELECT ? AS testcolumn", (pg/pg-json {:foo "bar"})])

	(defonce pool (pg/pg-pool :host "localhost" :dbname "testdb" :username "myuser" :password "apassword"))
	(jdbc/query pool ["SELECT 1"])


### Connecting to database

The pg-spec and pg-pool functions use PGHOST, PGPORT, PGUSER and PGDATABASE environment variables
and the ~/.pgpass file by default. The function arguments can be used to override the connection
parameters in the environment. E.g.:

	(def db (pg-pool :dbname "anotherdb"))
	(jdbc/query db ["SELECT 'test'"])

The pool can be closed with:

	(close-pooled-db! db)


## License

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
