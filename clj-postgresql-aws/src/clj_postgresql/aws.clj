(ns clj-postgresql.aws
  (:require [clj-postgresql.core :as pg])
  (:import (com.amazonaws.services.rds.auth RdsIamAuthTokenGenerator GetIamAuthTokenRequest)
           (com.amazonaws.auth DefaultAWSCredentialsProviderChain)
           (com.amazonaws.regions DefaultAwsRegionProviderChain)))

(defn rds-env-spec
  "Make a db spec map from RDS_* variables in environment. Elastic Beanstalk uses these variables."
  []
  (let [rds-hostname (System/getenv "RDS_HOSTNAME")
        rds-port (System/getenv "RDS_PORT")
        rds-db-name (System/getenv "RDS_DB_NAME")
        rds-username (System/getenv "RDS_USERNAME")
        rds-password (System/getenv "RDS_PASSWORD")]
    (cond-> {}
            rds-db-name (assoc :dbname rds-db-name)
            rds-hostname (assoc :host rds-hostname)
            rds-port (assoc :port rds-port)
            rds-username (assoc :user rds-username)
            rds-password (assoc :password rds-password))))

(defn rds-spec
  "Make a db spec that uses the RDS_* and PG* environment variables to connect to the database."
  ([opts]
   (let [rds-opts (rds-env-spec)
         merged-opts (merge rds-opts opts)]
     (pg/spec merged-opts)))
  ([]
   (rds-spec {})))

(defn make-auth-token [{:keys [host port user] :as opts}]
  #_(println "opts:" opts)
  (let [credentials-provider (DefaultAWSCredentialsProviderChain.)
        region ^String (.getRegion (DefaultAwsRegionProviderChain.))
        token-generator (-> (RdsIamAuthTokenGenerator/builder)
                            (.credentials credentials-provider)
                            (.region region)
                            (.build))
        token-request (-> (GetIamAuthTokenRequest/builder)
                          (.hostname host)
                          (.port (or (when port (Integer/parseInt port)) 5432))
                          (.userName user)
                          (.build))]
    #_(println "region:" region)
    (-> token-generator
        (.getAuthToken token-request))))

(defn rds-iam-spec
  "Make a db spec with IAM auth token (if no password given)."
  ([opts]
   (let [spec-opts (rds-spec opts)]
     (cond-> spec-opts
             (not (contains? spec-opts :password)) (assoc :password (make-auth-token spec-opts)))))
  ([]
   (rds-iam-spec {})))
