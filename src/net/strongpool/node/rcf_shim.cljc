(ns net.strongpool.node.rcf-shim)

#?(:bb nil
   :clj (require '[hyperfiddle.rcf]))

#_:clj-kondo/ignore
(defmacro tests [& body]
  #?(:bb `(clojure.core/comment ~@body)
     :clj `(hyperfiddle.rcf/tests ~@body)))
