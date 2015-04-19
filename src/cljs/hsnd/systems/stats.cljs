(ns hsnd.systems.stats
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn- stat-query [tag name] (str "//" tag "[@id='stats-" name "']"))
(defn- stat-view-raw [tag name] (xpath/xpath (stat-query tag name)))
(def stat-view (memoize stat-view-raw))

(defn- set-stat-text! [name representation] (dom/set-text! (stat-view "div" name) representation))
(defn- set-alternate-stat-text! [name representation] (dom/set-text! (stat-view "span" name) representation))

(defn- get-stat-component
  [player stat-name default]
  (entity/get-with-defaults player stat-name {:value default}))

(defn- get-stat
  [player stat-name default]
  (-> (get-stat-component player stat-name default) (component/get :value)))

(defn- set-stat
  [player stat-name value]
  (-> (get-stat-component player stat-name nil) (component/set :value value))
  value)

(defn- simple-render
  [[stat-name value]]
  (let [stat-title (apply str (take 3 stat-name))
        representation (str (clojure.string/upper-case stat-title) ": " value)]
    (set-stat-text! stat-name representation)))

(defn- render-stats
  [stats]
  (doall
   (map simple-render stats)))

(defn- items-stat-fn
  [stat acc item]
  (let [value (-> (entity/get-with-defaults item "stats" {}) (component/get stat))]
    (if value
      (-> acc (+ value))
      acc)))

(defn- get-items-stat
  [actor stat]
  (if (entity/get actor "player")
    (let [equipped-items (entity/each "equipped")]
      (reduce (partial items-stat-fn stat) 0 equipped-items))
    0))

(defn- draw-player-stats
  [player]
  (let [level (get-stat player "level" 0)
        level-representation (str "Level " level)

        max-xp (get-stat player "max-xp" 100)
        xp (get-stat player "xp" 0)
        xp-representation (str "EXP: " xp "/" max-xp)

        skill-points (get-stat player "skill-points" 1)
        press-c-tip (if (-> skill-points (> 0)) " [press C]" "")
        skill-points-representation (str "SP: " skill-points press-c-tip)
        skill-points-alternate-representation (if (-> skill-points (> 0))
                                                (str " (" skill-points " skill points)")
                                                "")

        max-health (get-stat player "max-health" 100)
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

    (set-stat-text! "level" level-representation)
    (set-stat-text! "exp" xp-representation)

    (set-stat-text! "skill-points" skill-points-representation)
    (set-alternate-stat-text! "skill-points" skill-points-alternate-representation)

    (if alive?
      (let [representation (str "HP: " health "/" max-health)]
        (set-stat-text! "health" representation))
      (let [resurrect-in (component/get resurrection-timeout :value)
            representation (str "RESPAWN: " resurrect-in)]
        (set-stat-text! "health" representation)))))

(defn- calculate-actor-stats
  [actor]
  (let [strength (get-stat actor "strength" 1)
        endurance (get-stat actor "endurance" 1)

        item-damage (get-items-stat actor :damage)
        base-damage (set-stat actor "base-damage" strength)
        damage-modifier (set-stat actor "damage-modifier" (-> strength (quot 14) (* 0.1)))
        damage (set-stat actor "damage" (-> base-damage (+ item-damage) (* (-> 1 (+ damage-modifier))) (int)))

        item-health (get-items-stat actor :health)
        base-health (set-stat actor "base-health" (-> strength (* 35) (+ (-> endurance (* 65)))))
        health-modifier (set-stat actor "health-modifier" (-> strength (+ endurance) (quot 14) (* 0.1)))
        max-health (set-stat actor "max-health" (-> base-health (+ item-health) (* (-> 1 (+ health-modifier))) (int)))
        health (get-stat actor "health" max-health)

        item-armor (get-items-stat actor :armor)
        base-armor (set-stat actor "base-armor" endurance)
        armor-modifier (set-stat actor "armor-modifier" (-> endurance (quot 14) (* 0.1)))
        armor (set-stat actor "armor" (-> base-armor (+ item-armor) (* (-> 1 (+ armor-modifier))) (int)))

        item-regen (get-items-stat actor :regen)
        base-regen (set-stat actor "base-regen" (-> 1 (+ (-> endurance (quot 5)))))
        regen (set-stat actor "regen" (-> base-regen (+ item-regen)))

        level (get-stat actor "level" 0)
        max-xp (set-stat actor "max-xp" (-> 10 (* (Math/pow 1.3 level)) (int)))]
    nil))

(defn draw
  []
  (entity/each "player-controlled" draw-player-stats))

(defn update
  []
  (entity/each "has-stats" calculate-actor-stats))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
