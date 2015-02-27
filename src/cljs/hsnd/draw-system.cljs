(ns hsnd.draw-system
  (:require [domina :as dom]
            [domina.xpath :as xpath]
            [hsnd.component :as component]
            [hsnd.entity :as entity]))

(def width 40)
(def height 40)

(def view-query "//div[@id='main']")
(def view (xpath/xpath view-query))

(defn- get-row
  [row]
  (-> view
      (xpath/xpath (str "p[" row "]"))))

(defn- init-view
  []
  (dom/set-text! view "")
  (let [blank (apply str (repeat width "."))]
    (loop [row 1]
      (if (<= row height)
        (do
          (dom/append! view (str "<p>" blank "</p>"))
          (recur (inc row)))
        nil))))

(defn- draw-tile
  [{x :x y :y} {value :value} tiles]
  (let [row (assoc! (tiles y) x value)]
    (assoc! tiles y row)))

(defn- draw-empty-column
  [x -tiles]
  (loop [y 1
         tiles -tiles]
    (if (<= y height)
      (recur (inc y) (draw-tile {:x x :y y} {:value "."} tiles))
      tiles)))

(defn- draw-empty
  [-tiles]
  (loop [x 1 tiles -tiles]
    (if (<= x width)
      (recur (inc x) (draw-empty-column x tiles))
      tiles)))

(defn- draw-entity
  [entity tiles]
  (let [tile (entity/get entity "tile")
        position (entity/get entity "position")]
    (if-not (nil? position)
      (draw-tile
       (component/get-hash position)
       (component/get-hash tile)
       tiles)
      tiles)))

(def empty-tiles (transient (vec (repeatedly (inc height) #(transient {})))))

(defn- draw-tile-row
  [y row]
  (let [content (apply str (map #(row %) (sort (keys row))))]
    (-> (get-row y)
        (dom/set-text! content))))

(defn- draw-tiles
  [tiles]
  (.log js/console tiles)
  (loop [row 1]
    (if (<= height)
      (do
        (draw-tile-row row (tiles row))
        (recur (inc row))))))

(defn- draw-step
  [tiles entity]
  (draw-entity entity tiles))

(defn init [] (init-view))

(defn update [] nil)

(defn keydown [key-code] nil)

(defn keyup [] nil)

(defn draw
  []
  (let [entities (component/entities (component/by-name "tile"))
        tiles (draw-empty empty-tiles)]
    (draw-tiles (reduce draw-step tiles entities))))
  ;#_(loop [entities (component/entities (component/by-name "tile"))
  ;       tiles (draw-empty empty-tiles)]
  ;  (if (empty? entities)
  ;    (draw-tiles tiles)
  ;    (let [entity (peek entities)]
  ;      (recur (pop entities) tiles)))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
