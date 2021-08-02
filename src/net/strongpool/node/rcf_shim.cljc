(ns net.strongpool.node.rcf-shim)

#?(:clj (require '[hyperfiddle.rcf]))

#_:clj-kondo/ignore
(defmacro tests [& body]
  #?(:clj `(hyperfiddle.rcf/tests ~@body)))
