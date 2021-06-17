(ns net.strongpool.node.config
  (:refer-clojure :exclude [load])
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   #?(:bb  [spartan.spec :as s]
      :default [clojure.spec.alpha :as s])))

#?(:bb (alias 's 'clojure.spec.alpha))

(def digest-regex #"(?i)^[a-z0-9-_]{43}$")
(def ipv4-address-regex #"^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$")

(s/def ::ipv4-address (s/and string? #(re-matches ipv4-address-regex %)))
(s/def ::miner-address (s/and string? #(re-matches digest-regex %)))
(s/def ::peers (s/coll-of ::ipv4-address))
(s/def ::extra-arg (s/and string? not-empty))
(s/def ::extra-args (s/coll-of ::extra-args))
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

(def config-filename "config/strongpool.edn")

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
