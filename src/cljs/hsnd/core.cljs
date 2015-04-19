(ns hsnd.core
  (:require [hsnd.systems.draw :as draw]
            [hsnd.systems.control :as control]
            [hsnd.systems.movement :as movement]
            [hsnd.systems.collision :as collision]
            [hsnd.systems.blocked :as blocked]
            [hsnd.systems.battle :as battle]
            [hsnd.systems.stats :as stats]
            [hsnd.systems.xp :as xp]
            [hsnd.systems.death :as death]
            [hsnd.systems.regeneration :as regeneration]
            [hsnd.systems.resurrection :as resurrection]
            [hsnd.systems.loot :as loot]
            [hsnd.systems.busy :as busy]
            [hsnd.systems.inventory :as inventory]
            [hsnd.systems.levelup :as levelup]
            [hsnd.systems.melee-ai :as melee-ai]
            [hsnd.systems.current-position :as current-position]
            [hsnd.systems.log :as log]
            [hsnd.callback :as callback]
            [hsnd.initial :as initial]
            [domina.events :as events]))

(def fps 60)
(def interval (-> 1000.0 (/ fps)))

(def systems [draw/system
              current-position/system
              stats/system
              xp/system
              control/system
              busy/system
              inventory/system
              levelup/system
              collision/system
              regeneration/system
              resurrection/system
              movement/system
              blocked/system
              battle/system
              death/system
              loot/system
              melee-ai/system
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
  [key-code {keyup :keyup}]
  (keyup key-code))

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
  (let [key-code (:keyCode event)]
    (run-all (partial keyup key-code))))

(js/setInterval handle-frame interval)
(events/listen! :keydown handle-keydown)
(events/listen! :keyup handle-keyup)
