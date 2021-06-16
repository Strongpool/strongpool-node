(ns net.strongpool.node.config
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(def base-config
  {:arweave
   {:peers ["188.166.200.45" "188.166.192.169" "163.47.11.64" "139.59.51.59" "138.197.232.192"]}})

(def config-filename "config.edn")

;; TODO validate config against a spec

(defn load []
  (if (-> config-filename io/file .exists)
    (->> config-filename
         slurp
         edn/read-string
         (merge-with merge base-config))
    base-config))
