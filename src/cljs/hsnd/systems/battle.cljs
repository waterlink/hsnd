(ns hsnd.systems.battle
  (:require [hsnd.entity :as entity]
            [hsnd.component :as component]
            [hsnd.callback :as callback]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn update [] nil)

(defn- target-query [tag name] (str "//" tag "[@id='target-" name "']"))
(defn- target-view-raw [tag name] (xpath/xpath (target-query tag name)))
(def target-view (memoize target-view-raw))

(defn- set-target-text! [name representation] (dom/set-text! (target-view "div" name) representation))

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
      (-> (entity/get-with-defaults entity "last-attacker" {:value nil})
          (component/set :value attacker))
      (-> (entity/get-with-defaults attacker "last-target" {:value nil})
          (component/set :value entity)))))

(defn- draw-player-target-stats
  [player]
  (when-let [target (entity/get-value player "last-target" nil)]
    (let [name (:name target)
          name-repr (str "Target: " name)

          level (entity/get-value target "level")
          level-repr (str "LVL: " level)

          health (entity/get-value target "health")
          alive? (nil? (entity/get target "dead"))
          health-repr (if alive? (str "HP: " health) "DEAD")]

      (set-target-text! "name" name-repr)
      (set-target-text! "level" level-repr)
      (set-target-text! "health" health-repr))))

(defn draw
  []
  (entity/each "player-controlled" draw-player-target-stats))

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
