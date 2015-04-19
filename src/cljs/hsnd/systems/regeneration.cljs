(ns hsnd.systems.regeneration
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn draw [] nil)

(def full-round 30)

(defn- get-stat
  [actor stat default]
  (-> (entity/get-with-defaults actor stat {:value default}) (component/get :value)))

(defn- set-stat
  [actor stat value]
  (-> (entity/get actor stat) (component/set :value value)))

(defn- regenerate
  [actor]
  (let [max-health (get-stat actor "max-health" 100)
        health (get-stat actor "health" max-health)
        regen (get-stat actor "regen" 0)
        regen (max regen 0)
        new-health (-> health (+ regen) (min max-health))]
    (set-stat actor "health" new-health)))

(defn- regenerate-round
  [actor]
  (let [regen-round (get-stat actor "regen-round" 0)
        new-regen-round (-> regen-round (inc) (mod full-round))]
    (set-stat actor "regen-round" new-regen-round)
    (when (-> new-regen-round (= 0))
      (regenerate actor))))

(defn update
  []
  (entity/each "regen" regenerate-round))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
