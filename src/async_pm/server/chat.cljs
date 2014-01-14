(ns async-pm.server.chat
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.nodejs :as node]
            [cljs.core.async :refer [>! <! chan close! put!]]))

(enable-console-print!)

;;;; ======================================================================
;;;; node.js 

(def http (node/require "http"))
(def express (node/require "express"))
(def socket (node/require "socket.io"))

(defn setup! [port]
  (let [webapp (express)
        server (->> (doto (.createServer http webapp) (.listen port))
                    (.listen socket))]
    (.use webapp (.static express (str js/__dirname "/../../public/")))
    server))

;;;; ======================================================================
;;;; Aplicacao

(def clients (atom [])) ;; Estado

(defn outboxes [] (vec (map #(:out %) @clients)))

(defn client-handler [s {:keys [in out]}]
  (.on s "message" #(go (>! in %)))
  (go-loop [[val ch] ["Bem vindo" out]]
           (when-not (nil? val)
             (condp = ch
               out (.send s val)
               in  (doseq [o (outboxes)] (put! o val))
               :other)
             (recur (alts! [in out])))))

(defn conn-handler [server]
  (let [conn (chan)
        disc (chan)]
    (.on (.-sockets server) "connection" #(go (>! conn %)))
    (go-loop [[val ch] []]
             (condp = ch
               conn (let [client {:in (chan) :out (chan)}]
                      (swap! clients conj client)
                      (.on val "disconnect" #(go (>! disc client)))
                      (client-handler val client))
               disc (do
                      (swap! clients (partial remove #(= % val)))
                      (close! (:in val))
                      (close! (:out val)))
               :other)
             (recur (alts! [conn disc])))))

(defn main []
  (-> (setup! 4000)
      (conn-handler)))
                                        
(set! *main-cli-fn* main)
