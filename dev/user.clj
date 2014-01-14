(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require [cljs.repl.node :as node]))

(defn node [] (node/run-node-nrepl))

