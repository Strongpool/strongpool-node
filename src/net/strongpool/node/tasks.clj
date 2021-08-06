(ns net.strongpool.node.tasks
  (:require [babashka.process :refer [check process]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [net.strongpool.node.config :as config]
            [net.strongpool.node.rcf-shim :refer [tests]]))

(defn arweave-args [config]
  (let [{:keys [mine? miner-address arweave]} config
        {:keys [extra-args peers]} arweave]
    (cond-> []
      mine?
      (conj "mine")

      miner-address
      (into ["mining_addr" miner-address])

      extra-args
      (into extra-args)

      peers
      (into (interleave (repeat "peer") peers)))))

(defn arweave-args-str [config]
  (str/join " " (arweave-args config)))

(defn arweave-env-vars [config]
  (let [{:keys [debug?]} config
        {:keys [egress-rate-limit]} (:arweave config)]
    (cond-> {}
      debug?
      (assoc :DEBUG "1")

      egress-rate-limit
      (assoc :EGRESS_RATE_LIMIT egress-rate-limit))))

;; TODO unified handling of exceptions thrown by 'check'

(defn start []
  (when-let [config (config/validated-load)]
    (let [args (arweave-args-str config)]
      (println "Starting Strongpool node... ")
      ;; TODO stream output
      (-> (process ["docker-compose" "up" "-d"]
                   {:out :string
                    :env (merge {:PATH (System/getenv "PATH")
                                 :ARWEAVE_ARGS args
                                 :ARWEAVE_IMAGE_REPO (or (System/getenv "ARWEAVE_IMAGE_REPO")
                                                         (get-in config [:arweave :image-repo]))}
                                (arweave-env-vars config))})
          check
          :out
          print)
      (println "Strongpool node started."))))

(defn stop []
  (println "Stopping Strongpool node... ")
  (when-let [config (config/validated-load)]
    ;; TODO stream output
    (-> (process ["docker-compose" "exec" "-d" "arweave" "/arweave/bin/stop"]
                 {:out :string
                  :env {:PATH (System/getenv "PATH")
                        :ARWEAVE_IMAGE (or (System/getenv "ARWEAVE_IMAGE")
                                           (get-in config [:arweave :image]))}})
        check
        :out
        print)
    ;; TODO wait for arweave service to actually stop
    (println "Strongpool node stopped.")))

;; TODO why does this end up clearing the screen part way through
(defn logs []
  (let [p (process ["docker-compose" "logs"]) ]
    (with-open [rdr (io/reader (:out p))]
      (binding [*in* rdr]
        (loop []
          (when-let [line (read-line)]
            (println line)
            (recur)))))))

(defn validate-config []
  (when-let [config (config/validated-load)]
    (println "Valid Strongpool config:")
    (pprint config)))

;; TODO add 'dc' (docker-compose) command

(tests

 ;; Arweave args tests

 (def empty-config {})

 (arweave-args empty-config) := []

 (def miner-config {:mine? true
                    :miner-address "Tk1NuG7Jxr9Ecgva5tWOJya2QGDOoS6hMZP0paB129c"})

 (arweave-args miner-config) :=
 ["mine" "mining_addr" "Tk1NuG7Jxr9Ecgva5tWOJya2QGDOoS6hMZP0paB129c"]

 (def config-with-peers (-> miner-config
                            (assoc-in [:arweave :peers] #{"188.166.200.45"
                                                          "188.166.192.169"})))

 (arweave-args config-with-peers) :=
 ["mine" "mining_addr" "Tk1NuG7Jxr9Ecgva5tWOJya2QGDOoS6hMZP0paB129c"
  "peer" "188.166.192.169" "peer" "188.166.200.45"]

 (def config-with-extra-args (-> miner-config
                                 (assoc-in [:arweave :extra-args] ["sync_jobs" "10"])))

 (arweave-args config-with-extra-args) :=
 ["mine" "mining_addr" "Tk1NuG7Jxr9Ecgva5tWOJya2QGDOoS6hMZP0paB129c"
  "sync_jobs" "10"]

 )
