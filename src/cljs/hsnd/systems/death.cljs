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

(defn- log-death
  [entity]
  (callback/emit :log-message (str (entity :name) " dies")))

(defn- handle-death
  [entity]
  (let [health-component (entity/get entity "health")
        health (component/get health-component :value)
        dead? (-> health (<= 0))
        not-handled-death? (nil? (entity/get entity "dead"))]
    (if (-> dead? (and not-handled-death?))
      (let [position-hash (component/get-hash (entity/get entity "position"))]
        (handle-loot entity)
        (entity/remove entity "position")
        (entity/remove entity "next-position")
        (entity/add entity "dead" {})
        (entity/add entity "position-of-death" position-hash)
        (log-death entity)))))

(defn update
  []
  (entity/each "health" handle-death))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
