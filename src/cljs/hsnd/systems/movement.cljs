(ns hsnd.systems.movement
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn update-entity
  [entity]
  (let [position (entity/get entity "position")
        next-position (entity/get entity "next-position")]
    (component/reset position (component/get-hash next-position))))

(defn update
  []
  (entity/each "next-position" update-entity))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
