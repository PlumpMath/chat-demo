(ns async-pm.server.macros)

;; Lancar excepcao se o valor retirado do canal e' um erro
;; Tirado de https://gist.github.com/swannodette/6385166
(defmacro <? [expr]
  `(async-pm.server.helpers/throw-err (cljs.core.async/<! ~expr)))
