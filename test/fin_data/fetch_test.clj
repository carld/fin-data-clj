(ns fin-data.fetch-test
  (:require [clojure.test :refer :all]
            [fin-data.fetch :refer :all]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [clj-time.coerce :as c]))

(deftest parse-date-test
  (testing "parse-date"
    (is (= (parse-date "12 Feb 2017")
           (t/from-time-zone
            (t/date-time 2017 02 12 9 0 0 0)
            (t/time-zone-for-id "Pacific/Auckland"))))))

(deftest rates-test
  (testing "rates"
    (is (= (fin-data.fetch/rates
            [["<p>21 April 1999</p>"
              "<p>No change</p>"
              "<p>4.50</p>"]])

           [{:announced_on
             (l/format-local-time
              (t/from-time-zone
               (t/date-time 1999 4 21 9 0 0 0)
               (t/time-zone-for-id "Pacific/Auckland") )
              :date-time
              )
             :link nil
             :rate 4.50
              }]
           ))
    ))
