(ns net.strongpool.node-tasks
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.java.shell :as shell]
   [clojure.string :as str]))

(def default-config-filename ".default-config.edn")
(def user-config-filename "config.edn")

(defn checked-sh [& args]
  (let [res (apply shell/sh args)]
    (if (not= 0 (:exit res))
      (throw (ex-info "Error returned from shell command" res))
      res)))

;; TODO validate config against a spec

(defn get-config []
  (let [default-config (-> default-config-filename
                           slurp
                           edn/read-string)]
    (if (-> user-config-filename io/file .exists)
      (->> user-config-filename
           slurp
           edn/read-string
           (merge-with merge default-config))
      default-config)))

;; TODO determine why 'bash -c' is needed to get $PATH right
;; TODO create a shell/sh wrapper with default error and output handling
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
