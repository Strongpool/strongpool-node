(ns net.strongpool.node
  (:require [docopt.core :as docopt]
            [net.strongpool.node.commands :as cmds]))

(def usage "Strongpool Node Controller

Usage:
  spnctl start
  spnctl stop
  spnctl logs
  spnctl validate-config")

(defn -main [& args]
  (docopt/docopt usage args cmds/run-command))

(comment

  (docopt/docopt usage ["start"] #(prn :arg-map %))

  )
