(ns fin-data.graphql
  (:require [graphql-clj.parser :as parser]
            [graphql-clj.type :as type]
            [graphql-clj.resolver :as resolver]
            [graphql-clj.executor :as executor]
            [graphql-clj.validator :as validator]
            [graphql-clj.introspection :as introspection]
            [clojure.core.match :as match]
            [clojure.java.jdbc :as sql]
            [environ.core :as env]))

(def db-spec
  {:connection-uri
   (env/env :database-url "jdbc:postgresql://localhost:5432/fin-data")})

(def fin-data-schema "
type OfficialCashRates {
  announced_on: String!
  rate: String!
  link: String
}

type Query {
  official_cash_rates: [OfficialCashRates]
}

schema {
  query: Query
}
")

(defn get-official-cash-rates
  []
  (sql/with-db-connection [db db-spec]
    (try
      (sql/query
       db
       ["SELECT * FROM official_cash_rates ORDER BY announced_on DESC"])
      (catch Exception _))))

(defn starter-resolver-fn
  [type-name field-name]
  (match/match
   [type-name field-name]
   ["Query" "official_cash_rates"] (fn [context parent args]
                                     (get-official-cash-rates))
   :else nil))

(def parsed-schema (parser/parse fin-data-schema))

(defn execute
  [query variables]
  (let [type-schema (validator/validate-schema parsed-schema)
        context nil]
    (executor/execute context type-schema starter-resolver-fn query variables)))
