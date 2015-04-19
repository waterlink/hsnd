(ns hsnd.systems.stats
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn- stat-query [name] (str "//div[@id='stats-" name "']"))
(defn- stat-view-raw [name] (xpath/xpath (stat-query name)))
(def stat-view (memoize stat-view-raw))

(defn- set-stat-text! [name representation] (dom/set-text! (stat-view name) representation))

(defn- get-stat-component
  [player stat-name default]
  (entity/get-with-defaults player stat-name {:value default}))

(defn- get-stat
  [player stat-name default]
  (-> (get-stat-component player stat-name default) (component/get :value)))

(defn- set-stat
  [player stat-name value]
  (-> (get-stat-component player stat-name nil) (component/set :value value)))

(defn- simple-render
  [[stat-name value]]
  (let [stat-title (apply str (take 3 stat-name))
        representation (str (clojure.string/upper-case stat-title) ": " value)]
    (set-stat-text! stat-name representation)))

(defn- render-stats
  [stats]
  (doall
   (map simple-render stats)))

(defn- items-damage-fn
  [acc item]
  (let [damage (-> (entity/get-with-defaults item "stats" {}) (component/get :damage))]
    (if damage
      (-> acc (+ damage))
      acc)))

(defn- get-items-damage
  [player]
  (let [equipped-items (entity/each "equipped")]
    (reduce items-damage-fn 0 equipped-items)))

(defn- draw-player-stats
  [player]
  (let [max-health (get-stat player "max-health" 100)
        health (get-stat player "health" max-health)
        strength (get-stat player "strength" 1)
        endurance (get-stat player "endurance" 1)
        damage (get-stat player "damage" 1)
        armor (get-stat player "armor" 1)
        regen (get-stat player "regen" 1)
        resurrection-timeout (entity/get player "resurrection-timeout")
        alive? (nil? resurrection-timeout)]

    (render-stats {"strength" strength
                   "endurance" endurance
                   "damage" damage
                   "armor" armor
                   "regen" regen})

    (if alive?
      (let [representation (str "HP: " health "/" max-health)]
        (set-stat-text! "health" representation))
      (let [resurrect-in (component/get resurrection-timeout :value)
            representation (str "RESPAWN: " resurrect-in)]
        (set-stat-text! "health" representation)))))

(defn- calculate-player-stats
  [player]
  (let [strength (get-stat player "strength" 1)
        endurance (get-stat player "endurance" 1)
        item-damage (get-items-damage player)
        base-damage (set-stat player "base-damage" strength)
        damage-modifier (set-stat player "damage-modifier" (-> strength (quot 14)))
        damage (set-stat player "damage" (-> base-damage (+ item-damage) (* (-> 1 (+ damage-modifier)))))]
    (.log js/console item-damage)
    nil))

(defn draw
  []
  (entity/each "player-controlled" draw-player-stats))

(defn update
  []
  (entity/each "player-controlled" calculate-player-stats))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
