(ns async-pm.server.helpers)

;; Tirado de https://gist.github.com/swannodette/6385166
(defn error? [x]
  (instance? js/Error x))

;; Tirado de https://gist.github.com/swannodette/6385166
(defn throw-err [x]
  (if (error? x)
    (throw x)
    x))
