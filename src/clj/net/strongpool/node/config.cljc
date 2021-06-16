(ns net.strongpool.node.config
  (:refer-clojure :exclude [load])
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   #?(:bb  [spartan.spec :as s]
      :default [clojure.spec.alpha :as s])))

#?(:bb (alias 's 'clojure.spec.alpha))

(s/def ::miner-address string?)         ; TODO validate with regex
(s/def ::peers (s/coll-of string?))     ; TODO validate with regex
(s/def ::extra-args (s/coll-of string?))
(s/def ::arweave (s/keys :req-un [::peers]
                         :opt [::extra-args]))
(s/def ::node-config (s/keys :req-un [::miner-address ::arweave]))

(def base-config
  {:arweave
   {:peers #{"188.166.200.45"
             "188.166.192.169"
             "163.47.11.64"
             "139.59.51.59"
             "138.197.232.192"}}})

(def config-filename "config.edn")

(defn load [filename]
  (if (fs/exists? filename)
    (->> filename
         #_:clj-kondo/ignore
         slurp
         edn/read-string
         (merge-with merge base-config))
    base-config))

(defn validated-load
  ([]
   (validated-load config-filename))
  ([filename]
   (let [config #_:clj-kondo/ignore (load filename)]
     (if (s/valid? ::node-config config)
       config
       (s/explain ::node-config config)))))

(comment

  #_:clj-kondo/ignore
  (load config-filename)

  (validated-load)

  )
