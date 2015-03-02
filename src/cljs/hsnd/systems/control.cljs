(ns hsnd.systems.control
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)

(defn draw [] nil)

(defn busy?
  [player]
  (-> (entity/get player "busy") (component/get :value)))

(defn move
  [key value]
  (let [components (component/by-name "player-controlled")
        player (first (entity/each "player"))]
    (if (not (busy? player))
      (doall (map #(component/set % key value) components)))))

(defn up [] (move :dy -1))
(defn left [] (move :dx -1))
(defn down [] (move :dy 1))
(defn right [] (move :dx 1))
(defn stop [] (move :dx 0) (move :dy 0))

(def controls {87 up
               65 left
               83 down
               68 right})

(defn keydown
  [key-code]
  (if (contains? controls key-code)
    ((controls key-code))))

(defn keyup
  [key-code]
  (stop))

(defn update-entity
  [entity]
  (let [{dx :dx dy :dy} (-> (entity/get entity "player-controlled") (component/get-hash))
        position (entity/get entity "position")]
    (if-not (nil? position)
      (let [next-position (entity/get-with-defaults
                           entity
                           "next-position"
                           (component/get-hash position))]
        (if (or (not= 0 dx) (not= 0 dy))
          (let [{x :x y :y} (component/get-hash position)
                nx (+ x dx)
                ny (+ y dy)]
            (component/reset next-position {:x nx :y ny})))))))

(defn handle-busy
  [player]
  (if (busy? player)
    (stop)))

(defn update
  []
  (entity/each "player-controlled" update-entity)
  (entity/each "player" handle-busy))

(def system {:init init
             :update update
             :draw draw
             :keydown keydown
             :keyup keyup})
