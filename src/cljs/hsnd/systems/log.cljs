(ns hsnd.systems.log
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn keydown [] nil)
(defn keyup [] nil)

(def show-count 15)

(def view-query "//div[@id='message-log']")
(def view (xpath/xpath view-query))

(defn- message-view
  [index]
  (xpath/xpath view (str "p[" (inc index) "]")))

(defn- draw-message
  [index message-component]
  (let [{message :value} (component/get-hash message-component)]
    (dom/set-text! (message-view index) message)))

(defn- rotate-logs
  "FIXME: should rotate logs, somehow"
  []
  nil)

(defn init
  []
  (dom/set-text! view "")
  (doall
   (repeatedly show-count #(dom/append! view "<p></p>"))))

(defn update
  []
  (rotate-logs))

(defn draw
  []
  (let [messages (component/by-name "log-message")
        hronologically (reverse messages)
        last-messages (take show-count hronologically)]
    (doall (map-indexed draw-message last-messages))))

(defn log-message
  [message]
  (let [message-entity (entity/new "a log message")]
    (entity/add message-entity "log-message" {:value message})))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:log-message log-message}})
