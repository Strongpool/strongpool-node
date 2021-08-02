(ns net.strongpool.node.rcf-shim)

#?(:clj (require '[hyperfiddle.rcf]))

(defmacro tests [& body]
  #?(:clj `(hyperfiddle.rcf/tests ~@body)))
