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
        player? (= "player" entity-name)
        other-enemy-component (entity/get other-entity "enemy")
        other-enemy? (not (nil? other-enemy-component))
        damage-component (entity/get entity "damage")
        has-damage? (not (nil? damage-component))]
    (if (-> player? (and other-enemy?) (and has-damage?))
      (let [damage (component/get damage-component :value)]
        (inflict-damage damage other-entity)
        (log-damage-by-player (component/get damage-component :value) other-name)))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:collision handle-collision}})
