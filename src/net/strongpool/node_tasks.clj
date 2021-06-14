(ns net.strongpool.node-tasks
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.java.shell :as shell]
   [clojure.string :as str]))

;; TODO cleanup config loading code
(def default-config (-> ".default-config.edn"
                        slurp
                        edn/read-string))

(def config (->> (if (.exists (io/file "config.edn"))
                   (-> "config.edn" slurp edn/read-string)
                   {})
                 (merge-with merge default-config)))

;; TODO determine why 'bash -c' is needed to get $PATH right

(defn start []
  (let [args (->> (get-in config [:arweave :peers])
                  (str/join " peer ")
                  (str "peer "))
        cmd (str "ARWEAVE_ARGS='" args "' docker-compose up -d")]
    (shell/sh "bash" "-c" cmd)))

(defn stop []
  ;; TODO determine whether separate stop command is necessary
  (shell/sh "bash" "-c" "docker-compose exec -d arweave /arweave/bin/stop")
  ;; (shell/sh "bash" "-c" "sleep 10")
  ;; (shell/sh "bash" "-c" "docker-compose stop")
  )
