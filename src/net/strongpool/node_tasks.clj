(ns net.strongpool.node-tasks
  (:require
   [clojure.string :as str]
   [clojure.java.shell :as shell]))

;; TODO determine why 'bash -c' is needed to get $PATH right

(defn start []
  (shell/sh "bash" "-c" "docker-compose start"))

(defn stop []
  (shell/sh "bash" "-c" "docker-compose exec -d arweave /arweave/bin/stop"))
