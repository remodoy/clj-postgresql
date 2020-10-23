(ns clj-postgresql.util
  (:import (java.sql ParameterMetaData ResultSetMetaData)))

(defn parameter-metadata->map
  "Convert ParameterMetaData to a map."
  [^ParameterMetaData md i]
  {:parameter-class     (.getParameterClassName md i)
   :parameter-mode      (.getParameterMode md i)
   :parameter-type      (.getParameterType md i)
   :parameter-type-name (.getParameterTypeName md i)
   :precision           (.getPrecision md i)
   :scale               (.getScale md i)
   :nullable?           (.isNullable md i)
   :signed?             (.isSigned md i)})

(defn result-set-metadata->map
  "Convert ResultSetMetaData to a map."
  [^ResultSetMetaData md i]
  {:catalog-name         (.getCatalogName md i)
   :column-class-name    (.getColumnClassName md i)
   :column-display-size  (.getColumnDisplaySize md i)
   :column-label         (.getColumnLabel md i)
   :column-type          (.getColumnType md i)
   :column-type-name     (.getColumnTypeName md i)
   :precision            (.getPrecision md i)
   :scale                (.getScale md i)
   :schema-name          (.getSchemaName md i)
   :table-name           (.getTableName md i)
   :auto-increment?      (.isAutoIncrement md i)
   :case-sensitive?      (.isCaseSensitive md i)
   :currency?            (.isCurrency md i)
   :definitely-writable? (.isDefinitelyWritable md i)
   :nullable?            (.isNullable md i)
   :read-only?           (.isReadOnly md i)
   :searchable?          (.isSearchable md i)
   :signed?              (.isSigned md i)
   :writable?            (.isWritable md i)})