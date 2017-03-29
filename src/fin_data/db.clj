(ns fin-data.db
  (:require [clojure.java.jdbc :as sql]
            [environ.core :as env]
            [fin-data.fetch])
  (:import (java.util UUID))
  (:refer-clojure :exclude [find]))

(def db-spec {:connection-uri (env/env :database-url "jdbc:postgresql://localhost:5432/fin-data")})

(defn initial-schema [db]
  (sql/db-do-commands db
                      [(sql/create-table-ddl "official_cash_rates"
                                             [[:id "SERIAL" "PRIMARY KEY"]
                                              [:announced_on "TIMESTAMP WITH TIME ZONE" "UNIQUE" "NOT NULL"]
                                              [:rate "NUMERIC(100,2)" "NOT NULL"]
                                              [:link "VARCHAR(512)"]])]))

(defn insert-rates [rs]
  (sql/with-db-transaction [t db-spec]
    (sql/insert-multi! t :official_cash_rates rs)))

(defn add-historical-rates [db]
  (insert-rates (fin-data.fetch/all)))

(defn run-and-record [db migration]
  (println "Running migration:" (:name (meta migration)))
  (migration db)
  (sql/insert! db "migrations" [:name :created_at]
                     [(str (:name (meta migration)))
                      (java.sql.Timestamp. (System/currentTimeMillis))]))

(defn migrate [& migrations]
  (sql/with-db-connection [db db-spec]
    (try (sql/db-do-commands db
          (sql/create-table-ddl "migrations"
                                [[:name :varchar "NOT NULL"]
                                 [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]]))
         (catch Exception _))

    (sql/db-do-commands db
                        (let [has-run? (let [result (sql/query db ["SELECT name FROM migrations"])]
                                         (set (map :name result)))]
                          (doseq [m migrations
                                  :when (not (has-run? (str (:name (meta m)))))]
                            (run-and-record db m))))))

(defn -main []
  (migrate #'initial-schema
           #'add-historical-rates))
