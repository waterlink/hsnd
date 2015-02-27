(ns hsnd.control-system
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)

(defn draw [] nil)

(defn keydown
  [key-code]
  (if (contains? controls key-code)
    ((controls key-code))))

(defn keyup
  []
  (stop))

(defn update
  []
  (let [entities (component/entities (component/by-name "player-controlled"))]
    (doall (map #(update-entity %) entities))))

(defn update-entity
  [entity]
  (let [{dx :dx dy :dy} (-> (entity/get entity "player-controlled") (component/get-hash))
        position (entity/get entity "position")]
    (if-not (nil? position)
      (if (or (not= 0 dx) (not= 0 dy))
        (let [{x :x y :y} (component/get-hash position)
              nx (+ x dx)
              ny (+ y dy)]
          (component/reset position {:x nx :y ny}))))))

(defn move
  [dx dy]
  (let [components (component/by-name "player-controlled")]
    (doall (map #(component/reset % {:dx dx :dy dy}) components))))

(defn up [] (move 0 -1))
(defn left [] (move -1 0))
(defn down [] (move 0 1))
(defn right [] (move 1 0))
(defn stop [] (move 0 0))

(def controls {87 up
               65 left
               83 down
               68 right})

(def system {:init init
             :update update
             :draw draw
             :keydown keydown
             :keyup keyup})
