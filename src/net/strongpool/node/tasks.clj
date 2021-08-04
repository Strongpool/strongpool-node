(ns net.strongpool.node.tasks
  (:require
   [babashka.process :refer [check process]]
   [clojure.java.io :as io]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [net.strongpool.node.config :as config]
   [net.strongpool.node.rcf-shim :refer [tests]]))

;; TODO generate vector rather than string of args
(defn arweave-args [config]
  (cond-> ""
    (:mine? config)
    (str " mine")

    (and (:mine? config) (:miner-address config))
    (str " mining_addr " (:miner-address config) " ")

    (get-in config [:arweave :extra-args])
    (str (str/join " " (get-in config [:arweave :extra-args])) " ")

    (get-in config [:arweave :peers])
    (str (->> (get-in config [:arweave :peers])
              (str/join " peer ")
              (str "peer ")))))

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
    (let [args (arweave-args config)]
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

  "generate minimal arweave args"
  (arweave-args {:mine? true}) := " mine"

  )
