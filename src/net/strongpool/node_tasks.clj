(ns net.strongpool.node-tasks
  (:require
   [clojure.java.io :as io]
   [clojure.java.shell :as shell]
   [clojure.string :as str]))

;; TODO determine why 'bash -c' is needed to get $PATH right

(defn start []
  (shell/sh "bash" "-c" "docker-compose start"))

(defn stop []
  (shell/sh "bash" "-c" "docker-compose exec -d arweave /arweave/bin/stop"))
