(ns hsnd.systems.resurrection
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [hsnd.callback :as callback]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(def resurrection-timeout 250)

(defn- abs
  [value]
  (if (-> value (< 0)) (- value) value))

(defn- resurrect-at
  [player stone]
  (let [stone-position (entity/get stone "position")
        position (component/get-hash stone-position)]
    (entity/add player "position" position)))

(defn- resurrect
  [player]
  (entity/remove player "resurrection-timeout")
  (entity/remove player "dead")
  (let [health-component (entity/get player "health")
        max-health (component/get (entity/get player "max-health") :value)]
    (component/set health-component :value max-health))
  (entity/each "resurrection-stone-active" (partial resurrect-at player))
  (callback/emit :log-message "player is resurrected"))

(defn- resurrection-turn
  [player]
  (let [is-player? (not (nil? (entity/get player "player")))]
    (if is-player?
      (let [resurrection (entity/get-with-defaults
                          player
                          "resurrection-timeout"
                          {:value resurrection-timeout})
            current-turn (component/get resurrection :value)
            new-turn (dec current-turn)]
        (if (-> current-turn (= 0))
          (resurrect player)
          (component/set resurrection :value new-turn))))))

(defn- deactivate-current-stone
  ([] (entity/each "resurrection-stone-active" deactivate-current-stone))
  ([stone] (entity/remove stone "resurrection-stone-active")))

(defn- activate-stone
  [stone]
  (entity/add stone "resurrection-stone-active" {})
  (callback/emit :log-message "you activated resurrection stone"))

(defn- deflect-enemy
  [stone enemy]
  (let [enemy-alive? (nil? (entity/get enemy "dead"))]
    (if enemy-alive?
      (let [position (entity/get stone "position")
            {x :x y :y} (component/get-hash position)
            enemy-position (entity/get enemy "position")
            {tx :x ty :y} (component/get-hash enemy-position)
            enemy-next-position (entity/get-with-defaults enemy "next-position" {:x tx :y ty})
            {nx :x ny :y} (component/get-hash enemy-next-position)
            resurrection-stone (entity/get stone "resurrection-stone")
            effect-radius (component/get resurrection-stone :radius)
            dx (-> x (- nx) (abs))
            dy (-> y (- ny) (abs))]
        (if (-> dx (<= effect-radius) (and (-> dy (<= effect-radius))))
          (component/reset enemy-next-position {:x tx :y ty}))))))

(defn- deflect-enemies
  [stone]
  (entity/each "enemy" (partial deflect-enemy stone)))

(defn handle-collision
  [entity other-entity]
  (let [player? (not (nil? (entity/get entity "player")))
        other-stone? (not (nil? (entity/get other-entity "resurrection-stone")))
        non-active-stone? (nil? (entity/get other-entity "resurrection-stone-active"))]
    (if (-> player? (and other-stone?) (and non-active-stone?))
      (do
        (deactivate-current-stone)
        (activate-stone other-entity)))))

(defn update
  []
  (entity/each "dead" resurrection-turn)
  (entity/each "resurrection-stone-active" deflect-enemies))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:collision handle-collision}})
