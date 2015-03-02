(ns hsnd.systems.current-position
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn update [] nil)

(def view-query "//div[@id='stats-position']")
(def view (xpath/xpath view-query))

(defn- draw-player-position
  [player]
  (let [position (entity/get player "position")
        position-of-death (entity/get player "position-of-death")
        dead? (not (nil? (entity/get player "dead")))
        position (if dead? position-of-death position)
        {x :x y :y} (component/get-hash position)
        representation (str "( " x "; " y " )")]
    (dom/set-text! view representation)))

(defn draw
  []
  (entity/each "player" draw-player-position))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
