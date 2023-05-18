(ns grierson.core
  (:require [grierson.system :as system]
            [donut.system :as ds])
  (:gen-class))

(defn -main
  []
  (println "running...")
  (ds/start ::system/production))
