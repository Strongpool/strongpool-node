(ns net.strongpool.node.tasks-test
  (:require
   [clojure.test :refer [deftest is]]
   [net.strongpool.node.tasks :as sut]))

(deftest arweave-args-test []
  ;; FIXME this is not a good test :)
  (is (= " mine" (sut/arweave-args {:mine? true}))))
