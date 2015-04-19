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
  [damage entity attacker]
  (let [health-component (entity/get entity "health")
        health? (not (nil? health-component))]
    (when health?
      (component/set
       health-component
       :value
       (-> (component/get health-component :value) (- damage)))
      (-> (entity/get-with-defaults entity "last-attacker" {:value nil}) (component/set :value attacker)))))

(defn handle-collision
  [entity other-entity]
  (let [entity-name (entity :name)
        other-name (other-entity :name)
        player? (not (nil? (entity/get entity "player")))
        enemy? (not (nil? (entity/get entity "enemy")))
        other-player? (not (nil? (entity/get other-entity "player")))
        other-enemy? (not (nil? (entity/get other-entity "enemy")))
        damage-component (entity/get-with-defaults entity "damage" {:value 0})
        damage-stat (component/get damage-component :value)
        armor (-> (entity/get-with-defaults other-entity "armor" {:value 0}) (component/get :value))
        damage (-> damage-stat (- armor) (max 0))]

    (when (-> player? (and other-enemy?))
      (inflict-damage damage other-entity entity)
      (log-damage-by-player damage other-name))

    (when (-> enemy? (and other-player?))
      (inflict-damage damage other-entity entity)
      (log-damage-by-enemy damage entity-name))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:collision handle-collision}})
