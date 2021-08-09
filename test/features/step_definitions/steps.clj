(ns net.strongpool.node.features.steps
  (:require
   [babashka.process :refer [check process]]
   [clojure.string :as str]
   [clojure.test :refer [is]]
   [lambdaisland.cucumber.dsl :refer [Before After Given Then]]))

(Before []
  (-> (process ["mkdir" "-p" "data/01"] {:out :string})) check)

(After []
  (-> (process ["./spnctl" "stop"] {:out :string})) check)

(Given "'./spnctl (.*)' was run" [state args-string]
  (let [args (str/split args-string #" ")
        p (process (concat ["./spnctl"] args) {:out :string})]
    (assoc state :spnctl-process p)))

(Then "the '(.*)' service should be running" [state service]
  (check (:spnctl-process state))
  (let [service-line (-> (process ["docker-compose" "ps" "--" service]  {:out :string})
                         check
                         :out
                         str/split-lines
                         last)]
    (is (not (re-find #"Exit" service-line)))
    state))
