(ns fin-data.fetch
  (:require [clj-http.client :as client]
            [hickory.select :as s]
            [hickory.render :as r]
            [clojure.string :as string]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]
            [clj-time.coerce :as c])
  (:use [hickory.core]))

(def ocr-source-url
  "http://www.rbnz.govt.nz/monetary-policy/official-cash-rate-decisions")

(defn fetch-page
  []
  (-> (client/get ocr-source-url)
      :body
      parse as-hickory))

(def page (fetch-page))

(defn rows
  "Extract table rows containing figures given web page"
  [p]
  (for [tr (s/select (s/descendant (s/class "page-body") (s/tag :table) (s/tag :tr)) p)
        :let [row (s/select (s/child (s/tag :td) (s/node-type :element))   tr)]
        :when (seq row)]
    (map r/hickory-to-html row)))

(defn parse-date
  "Given a date string in the format dd MMM YYYY
  returns an ISO8601 format date in the Pacific/Auckland time zone"
  [s]
  (let [[_ d m y] (vec (first  (re-seq #"(\d+)\s+(\w{3})\w*\s+(\d+)" s)))]
    (f/parse
     (f/formatter "dd MMM yyyy HH mm" (t/time-zone-for-id "Pacific/Auckland"))
     (format "%s %s %s 09 00" d m y ))))

(defn rates
  [r]
  (map (fn [row]
         (let [[date change rate] row]
           (hash-map
            :announced_on (-> (re-seq #"\d+\s+\w+\s+\d+" date)
                              first
                              parse-date
                              (l/format-local-time :date-time)
                              )
            :link (let [link (-> (re-seq #"href=\"(.+)\"" date)
                                 first
                                 second)]
                    (cond
                      link  (str "http://www.rbnz.govt.nz" link)
                      :else nil))
            :rate (-> (re-seq #"\d+\.\d+" rate)
                      first
                      read-string))))
       r))

(defn all
  []
  (-> page
      rows
      rest     ; ignore the first header row
      rates))
