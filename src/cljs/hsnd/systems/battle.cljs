(ns hsnd.systems.battle
  (:require [hsnd.entity :as entity]
            [hsnd.component :as component]
            [hsnd.callback :as callback]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn update [] nil)

(defn- log-damage-by-player
  [damage name]
  (callback/emit :log-message (str "you inflicted " damage " damage to " name)))

(defn- log-damage-by-enemy
  [damage name]
  (callback/emit :log-message (str name " inflicted " damage " damage to you")))

(defn- inflict-damage
  [damage entity]
  (let [health-component (entity/get entity "health")
        health? (not (nil? health-component))]
    (if health?
      (component/set
       health-component
       :value
       (-> (component/get health-component :value) (- damage))))))

(defn handle-collision
  [entity other-entity]
  (let [entity-name (entity :name)
        other-name (other-entity :name)
        player? (not (nil? (entity/get entity "player")))
        enemy? (not (nil? (entity/get entity "enemy")))
        other-player? (not (nil? (entity/get other-entity "player")))
        other-enemy? (not (nil? (entity/get other-entity "enemy")))
        damage-component (entity/get entity "damage")
        has-damage? (not (nil? damage-component))]
    (if (-> player? (and other-enemy?) (and has-damage?))
      (let [damage (component/get damage-component :value)]
        (inflict-damage damage other-entity)
        (log-damage-by-player damage other-name)))
    (if (-> enemy? (and other-player?) (and has-damage?))
      (let [damage (component/get damage-component :value)]
        (inflict-damage damage other-entity)
        (log-damage-by-enemy damage entity-name)))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:collision handle-collision}})
