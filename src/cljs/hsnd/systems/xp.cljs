(ns hsnd.systems.xp
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn draw [] nil)
(defn update [])

(defn- get-stat
  [actor stat default]
  (-> (entity/get-with-defaults actor stat {:value default}) (component/get :value)))

(defn- set-stat
  [actor stat value]
  (-> (entity/get actor stat) (component/set :value value)))

(defn- handle-possible-levelup
  [killer]
  (let [skill-points (get-stat killer "skill-points" 0)
        level (get-stat killer "level" 0)
        xp (get-stat killer "xp" 0)
        max-xp (get-stat killer "max-xp" 100)
        levelup? (-> xp (>= max-xp))]
    (when levelup?
      (set-stat killer "level" (-> level (+ 1)))
      (set-stat killer "skill-points" (-> skill-points (+ 2))))))

(defn handle-kill
  [victim killer]
  (let [level (get-stat victim "level" 0)
        gives-xp (get-stat victim "gives-xp" level)
        xp (get-stat killer "xp" 0)]
    (set-stat killer "xp" (-> xp (+ gives-xp)))
    (handle-possible-levelup killer)))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup
             :listeners {:kill handle-kill}})
