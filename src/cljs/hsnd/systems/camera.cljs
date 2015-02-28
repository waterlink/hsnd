(ns hsnd.systems.camera
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)

(defn update
  []
  nil)

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
