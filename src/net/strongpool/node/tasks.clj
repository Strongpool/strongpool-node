(ns net.strongpool.node.tasks
  (:require
   [babashka.process :refer [check process]]
   [clojure.java.io :as io]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [net.strongpool.node.config :as config]))

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

;; TODO unified handling of exceptions thrown by 'check'

(defn start []
  (when-let [config (config/validated-load)]
    (let [args (arweave-args config)]
      (print "Staring Stronpool node... ")
      ;; TODO stream output
      (-> (process ["docker-compose" "up" "-d"]
                   {:out :string
                    :env {:PATH (System/getenv "PATH")
                          :ARWEAVE_ARGS args
                          :ARWEAVE_IMAGE (or (System/getenv "ARWEAVE_IMAGE")
                                             (get-in config [:arweave :image]))}})
          check
          :out
          print)
      (println "started."))))

(defn stop []
  (print "Stopping Stronpool node... ")
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
    (println "stopped.")))

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
