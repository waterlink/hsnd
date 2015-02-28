(ns hsnd.core
  (:require [hsnd.systems.draw :as draw]
            [hsnd.systems.control :as control]
            [hsnd.systems.movement :as movement]
            [hsnd.systems.collision :as collision]
            [hsnd.systems.blocked :as blocked]
            [hsnd.systems.battle :as battle]
            [hsnd.systems.death :as death]
            [hsnd.systems.loot :as loot]
            [hsnd.systems.log :as log]
            [hsnd.callback :as callback]
            [hsnd.initial :as initial]
            [domina.events :as events]))

(def fps 60)
(def interval (/ 1000.0 fps))

(def systems [draw/system
              control/system
              collision/system
              movement/system
              blocked/system
              battle/system
              death/system
              loot/system
              log/system])

(doall (map #((% :init)) systems))

(doall (map (fn
              [system]
              (if (contains? system :listeners)
                (doall (map
                        (partial apply callback/listen)
                        (system :listeners)))))
            systems))

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
