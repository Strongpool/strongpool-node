(ns net.strongpool.node.tasks
  (:require
   [clojure.java.shell :as shell]
   [clojure.string :as str]
   [net.strongpool.node.config :as config]))

(defn checked-sh [& args]
  (let [res (apply shell/sh args)]
    (if (not= 0 (:exit res))
      (throw (ex-info "Error returned from shell command" res))
      res)))

;; TODO determine why 'bash -c' is needed to get $PATH right

(defn start []
  (let [config (config/load)
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
