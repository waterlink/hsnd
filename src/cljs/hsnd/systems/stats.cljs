(ns hsnd.systems.stats
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn update [] nil)

(def health-query "//div[@id='stats-health']")
(def health-view (xpath/xpath health-query))

(defn- draw-player-stats
  [player]
  (let [health (-> (entity/get player "health") (component/get :value))
        max-health (-> (entity/get player "max-health") (component/get :value))
        resurrection-timeout (entity/get player "resurrection-timeout")
        alive? (nil? resurrection-timeout)]
    (if alive?
      (let [representation (str "HP: " health "/" max-health)]
        (dom/set-text! health-view representation))
      (let [resurrect-in (component/get resurrection-timeout :value)
            representation (str "RESPAWN: " resurrect-in)]
        (dom/set-text! health-view representation)))))

(defn draw
  []
  (entity/each "player-controlled" draw-player-stats))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
