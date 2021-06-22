(ns net.strongpool.node.features.steps
  (:require
   [lambdaisland.cucumber.dsl :refer [When Then pending!]]))

(When "I run {string}" [_state _command]
  (pending!))

(Then "the node starts" [_state]
  (pending!))
