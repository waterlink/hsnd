(ns hsnd.systems.death
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [hsnd.callback :as callback]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn- handle-loot
  [entity]
  (let [position (entity/get entity "position")
        position? (not (nil? position))
        loot (entity/get entity "loot")
        loot? (not (nil? loot))]
    (if (-> loot? (and position?))
      (callback/emit :drop-loot
                     (component/get-hash position)
                     (component/get loot :value)
                     entity))))

(defn- handle-death
  [entity]
  (let [health-component (entity/get entity "health")
        health (component/get health-component :value)
        dead? (-> health (<= 0))
        dead-component (entity/get entity "dead")
        not-handled-death? (nil? dead-component)]
    (if (-> dead? (and not-handled-death?))
      (do
        (handle-loot entity)
        (entity/remove entity "position")
        (entity/add entity "dead" {})))))

(defn update
  []
  (entity/each "health" handle-death))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
