(ns hsnd.systems.draw
  (:require [domina :as dom]
            [domina.xpath :as xpath]
            [hsnd.component :as component]
            [hsnd.entity :as entity]))

(def width 40)
(def height 40)
(def half-width (quot width 2))
(def half-height (quot height 2))

(def view-query "//div[@id='main']")
(def view (xpath/xpath view-query))

(defn- camera-boundaries
  [camera-position]
  (let [{x :x y :y} (component/get-hash camera-position)]
    [(- x half-width) (+ x half-width)
     (- y half-height) (+ y half-height)]))

(defn- in-boundaries?
  [[low-x high-x low-y high-y] position]
  (let [{x :x y :y} (component/get-hash position)]
    (and (> x low-x) (<= x high-x)
         (> y low-y) (<= y high-y))))

(defn- relative-position
  [camera-position position]
  (let [{x :x y :y} (component/get-hash position)
        {cx :x cy :y} (component/get-hash camera-position)]
    {:x (-> x (- cx) (+ half-width))
     :y (-> y (- cy) (+ half-height))}))

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
  [camera entity tiles]
  (let [tile (entity/get entity "tile")
        position (entity/get entity "position")
        camera-position (entity/get camera "position")
        camera-position-of-death (entity/get camera "position-of-death")
        camera-position (if (nil? camera-position) camera-position-of-death camera-position)
        boundaries (camera-boundaries camera-position)]
    (if (and (not (nil? position)) (in-boundaries? boundaries position))
      (draw-tile
       (relative-position camera-position position)
       (component/get-hash tile)
       tiles)
      tiles)))

(defn empty-tiles
  []
  (transient (vec
              (repeatedly (inc height)
                          #(transient {})))))

(defn- draw-tile-row
  [y row]
  (let [content (apply str (map #(row %) (sort (keys row))))]
    (-> (get-row y)
        (dom/set-text! content))))

(defn- draw-tiles
  [transient-tiles]
  (let [tiles (persistent! transient-tiles)]
    (loop [row 1]
      (if (<= row height)
        (do
          (draw-tile-row row (persistent! (tiles row)))
          (recur (inc row)))))))

(defn- draw-step-for-passable
  [camera tiles entity]
  (let [passable? (not (nil? (entity/get entity "passable")))]
    (if passable?
      (draw-entity camera entity tiles)
      tiles)))

(defn- draw-step-for-not-passable
  [camera tiles entity]
  (let [not-passable? (nil? (entity/get entity "passable"))]
    (if not-passable?
      (draw-entity camera entity tiles)
      tiles)))

(defn init [] (init-view))

(defn update [] nil)

(defn keydown [key-code] nil)

(defn keyup [] nil)

(defn draw
  []
  (let [tiles (draw-empty (empty-tiles))
        camera (component/entity
                (first
                 (filter #(= true (component/get % :active))
                         (component/by-name "camera"))))
        tiles-with-passable (entity/reduce
                             "tile"
                             (partial draw-step-for-passable camera)
                             tiles)
        final-tiles (entity/reduce
                     "tile"
                     (partial draw-step-for-not-passable camera)
                     tiles)]
    (draw-tiles final-tiles)))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
