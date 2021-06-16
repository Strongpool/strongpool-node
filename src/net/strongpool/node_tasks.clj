(ns net.strongpool.node-tasks
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.java.shell :as shell]
   [clojure.string :as str]))

(def base-config
  {:arweave
   {:peers ["188.166.200.45" "188.166.192.169" "163.47.11.64" "139.59.51.59" "138.197.232.192"]}})

(def config-filename "config.edn")

;; TODO validate config against a spec

(defn get-config []
  (if (-> config-filename io/file .exists)
    (->> config-filename
         slurp
         edn/read-string
         (merge-with merge base-config))
    base-config))

(defn checked-sh [& args]
  (let [res (apply shell/sh args)]
    (if (not= 0 (:exit res))
      (throw (ex-info "Error returned from shell command" res))
      res)))

;; TODO determine why 'bash -c' is needed to get $PATH right
;; TODO add command descriptions

(defn start []
  (let [config (get-config)
        peer-args (->> (get-in config [:arweave :peers])
                       (str/join " peer ")
                       (str "peer "))
        other-args (->> (get-in config [:arweave :args])
                        (str/join " "))
        args (str other-args " " peer-args)
        cmd (str "ARWEAVE_ARGS='" args "' docker-compose up -d")]
    (print "Staring Stronpool node... ")
    (checked-sh "bash" "-c" cmd)
    (println "started.")))

(defn stop []
  (print "Stopping Stronpool node... ")
  (checked-sh "bash" "-c" "docker-compose exec -d arweave /arweave/bin/stop")
  (println "stopped.") ;; TODO wait for arweave service to actually stop
  )

(defn logs []
  (-> (checked-sh "bash" "-c" "docker-compose logs")
      :out
      println))
