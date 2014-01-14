(defproject async-pm "0.1.0-SNAPSHOT"
  :description "core.async demo for talk at Premium Minds"
  :url "https://github.com/dzacarias/async-pm"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]

  :plugins [[lein-npm "0.1.0"]
            [lein-cljsbuild "1.0.1"]
            [com.cemerick/clojurescript.test "0.2.1"]]

  :node-dependencies [[express "3.4.7"]
                      [socket.io "0.9.16"]]

  :source-paths ["src"]

  :cljsbuild { 
              :builds [{:id "chat-client"
                        :source-paths ["src/async_pm/client"]
                        :compiler {
                                   :output-to "public/client/chat.js"
                                   :output-dir "public/client"
                                   :optimizations :none
                                   :source-map "public/client/chat.js.map"}}
                       {:id "chat-server"
                        :source-paths ["src/async_pm/server"]
                        :compiler {
                                   :output-to "script/server/chat.js"
                                   :output-dir "script/server"
                                   :optimizations :simple
                                   :target :nodejs}}]})
