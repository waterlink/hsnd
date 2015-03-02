(ns hsnd.systems.melee-ai
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn- sign
  [value]
  (if (-> value (= 0))
    0
    (if (-> value (< 0)) (- 1) 1)))

(defn- abs
  [value]
  (if (-> value (< 0)) (- value) value))

(defn- next-move
  [its-position target-position]
  (let [direction (if (-> (.random js/Math) (< 0.5)) :x :y)
        other-direction ({:x :y :y :x} direction)
        move (sign (-> (target-position direction) (- (its-position direction))))]
    {direction move other-direction 0}))

(defn- chase
  [entity player]
  (let [its-position (component/get-hash (entity/get entity "position"))
        player-position (component/get-hash (entity/get player "position"))]
    (let [{dx :x dy :y} (next-move its-position player-position)
          {x :x y :y} its-position
          nx (-> x (+ dx))
          ny (-> y (+ dy))
          next-position-hash {:x nx :y ny}
          next-position (entity/get-with-defaults entity "next-position" next-position-hash)]
      (component/reset next-position next-position-hash))))

(defn- in-sight?
  [entity player]
  (let [{x :x y :y} (component/get-hash (entity/get entity "position"))
        {tx :x ty :y} (component/get-hash (entity/get player "position"))
        dx (-> x (- tx) (abs))
        dy (-> y (- ty) (abs))
        sight-radius (component/get (entity/get entity "sight") :radius)]
    (-> (-> dx (<= sight-radius)) (and (-> dy (<= sight-radius))))))

(defn- handle-player
  [entity player]
  (let [player-has-position? (not (nil? (entity/get player "position")))]
    (if player-has-position?
      (if (in-sight? entity player) (chase entity player)))))

(defn- handle-entity
  [entity]
  (let [has-position? (not (nil? (entity/get entity "position")))]
    (if has-position?
      (entity/each "player" (partial handle-player entity)))))

(defn update
  []
  (entity/each "melee-ai" handle-entity))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
