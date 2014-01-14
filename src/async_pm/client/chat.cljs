(ns async-pm.client.chat
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [cljs.core.async :refer [<! >! put! chan]]
            [clojure.string :refer [join]])
  (:import [goog.events KeyCodes]))

(enable-console-print!)

(defn evt-listen [el type]
  (let [ch (chan)]
    (events/listen el type #(put! ch %))
    ch))

(defn inbox [socket]
  (let [ch (chan)]
    (.on socket "message" #(put! ch %))
    ch))

(defn flush-msg! [ch]
  (let [el (dom/getElement "message")
        msg (.-value el)
        nick (.-value (dom/getElement "nick"))]
    (set! (.-value el) "")
    (go (>! ch (str nick ": " msg)))))

(defmulti ui-event (fn [v _] (.-type v)))

(defmethod ui-event "click" [v ch]
  (flush-msg! ch))

(defmethod ui-event "keyup" [v ch]
  (when (= (.-ENTER KeyCodes) (.-keyCode v))
    (flush-msg! ch)))

(defn print-message [msg]
  (dom/append
   (dom/getElement "log")
   (dom/createDom "li" nil msg)))

(defn gen-nickname []
  (let [nick 
        (str "Nick_" (join "" (take 5 (repeatedly #(rand-int 10)))))]
    (set! (.-value (dom/getElement "nick")) nick)
    nick))

(defn main [endpoint]
  (let [socket (js/io.connect endpoint)
        in     (inbox socket)
        out    (chan)
        clicks (evt-listen (dom/getElement "submit-button") "click")
        keys   (evt-listen (dom/getElement "message") "keyup")
        nick   (gen-nickname)]
    (go (while true
          (let [[v c] (alts! [clicks keys in out])]
            (condp = c
              in (print-message v)
              out (.send socket v)
              (ui-event v out)))))))

(main "http://localhost:4000")
