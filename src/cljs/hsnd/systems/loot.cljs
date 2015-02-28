(ns hsnd.systems.loot
  (:require [hsnd.entity :as entity]
            [hsnd.component :as component]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn update [] nil)

(defn- add-component-to-drop
  [drop [name hash]]
  (entity/add drop name hash))

(defn- handle-one-drop
  [position dropped-by [name components]]
  (let [drop (entity/new name)]
    (entity/add drop "position" position)
    (entity/add drop "dropped-by" dropped-by)
    (doall
     (map (partial add-component-to-drop drop) components))))

(defn handle-drop-loot
  [position loot dropped-by]
  (doall
   (map (partial handle-one-drop position dropped-by) loot)))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:drop-loot handle-drop-loot}})
