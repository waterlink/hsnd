(ns hsnd.systems.blocked
  (:require [hsnd.entity :as entity]
            [hsnd.component :as component]
            [hsnd.callback :as callback]))

(defn init [] nil)
(defn draw [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn update [] nil)

(defn handle-collision
  [entity other-entity]
  (let [player? (= "player" (entity :name))
        other-not-enemy? (nil? (entity/get other-entity "enemy"))]
    (if (-> player? (and other-not-enemy?))
      (callback/emit :log-message (str (other-entity :name) " is blocking your path")))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:collision handle-collision}})
