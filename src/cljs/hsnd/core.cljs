(ns hsnd.core
  (:require [hsnd.draw-system :as draw]
            [hsnd.control-system :as control]
            [hsnd.initial :as initial]
            [domina.events :as events]))

(def fps 10)
(def interval (/ 1000.0 fps))

(def systems [draw/system
              control/system])

(doall (map #((% :init)) systems))

(defn- run
  [{update :update draw :draw}]
  (update)
  (draw))

(defn- keydown
  [key-code {keydown :keydown}]
  (keydown key-code))

(defn- keyup
  [{keyup :keyup}]
  (keyup))

(defn- run-all [func] (doall (map func systems)))

(defn- handle-frame
  []
  (run-all run))

(defn- handle-keydown
  [event]
  (let [key-code (:keyCode event)]
    (run-all (partial keydown key-code))))

(defn- handle-keyup
  [event]
  (run-all keyup))

(js/setInterval handle-frame interval)
(events/listen! :keydown handle-keydown)
(events/listen! :keyup handle-keyup)
