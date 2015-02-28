(ns hsnd.systems.collision
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [hsnd.callback :as callback]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn- position-cell
  [entity position-kind]
  (component/get-hash (entity/get entity position-kind)))

(defn- cell
  [entity]
  (position-cell entity "position"))

(defn- next-cell
  [entity]
  (position-cell entity "next-position"))

(defn- cell-taken-by
  [cells-taken entity]
  (let [position (entity/get entity "position")
        cell (component/get-hash position)
        not-passable? (nil? (entity/get entity "passable"))]
    (if not-passable?
      (assoc! cells-taken cell entity)
      cells-taken)))

(defn- detect-taken-cells
  []
  (let [cells-taken (transient {})]
    (persistent! (entity/reduce "position" cell-taken-by cells-taken))))

(defn- collisions-for
  [cells-taken entity]
  (let [next-position (entity/get entity "next-position")
        position (entity/get entity "position")
        its-next-cell (next-cell entity)
        collision (cells-taken its-next-cell)]
    (if-not (or (nil? collision) (= entity collision))
      (do
        (component/reset next-position (component/get-hash position))
        (callback/emit :collision entity collision)))))

(defn update
  []
  (let [cells-taken (detect-taken-cells)]
    (entity/each "next-position" (partial collisions-for cells-taken))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
