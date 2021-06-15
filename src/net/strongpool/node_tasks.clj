(ns net.strongpool.node-tasks
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.java.shell :as shell]
   [clojure.string :as str]))

(def default-config-filename ".default-config.edn")
(def user-config-filename "config.edn")

;; TODO validate config against a spec

(defn get-config []
  (let [default-config (-> default-config-filename
                           slurp
                           edn/read-string)
        user-config-file (io/file user-config-filename)]
    (if (.exists user-config-file)
      (->> user-config-file
           io/reader
           slurp
           edn/read-string
           (merge-with merge default-config))
      default-config)))

;; TODO determine why 'bash -c' is needed to get $PATH right
;; TODO create a shell/sh wrapper with default error and output handling

(defn start []
  (let [config (get-config)
        peer-args (->> (get-in config [:arweave :peers])
                       (str/join " peer ")
                       (str "peer "))
        other-args (->> (get-in config [:arweave :args])
                        (str/join " "))
        args (str other-args " " peer-args)
        cmd (str "ARWEAVE_ARGS='" args "' docker-compose up -d")]
    (shell/sh "bash" "-c" cmd)))

(defn stop []
  (shell/sh "bash" "-c" "docker-compose exec -d arweave /arweave/bin/stop"))

(defn logs []
  (-> (shell/sh "bash" "-c" "docker-compose logs")
      :out
      println))
