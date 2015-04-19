(ns hsnd.systems.levelup
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn draw [] nil)

(def overlay-query "//div[@id='level-up-overlay']")
(def overlay-view (xpath/xpath overlay-query))

(defn- skill-query [name] (str "//div[@id='level-up-overlay']/div[@id='stats-" name "']"))
(defn- skill-view-raw [name] (xpath/xpath (skill-query name)))
(def skill-view (memoize skill-view-raw))

(defn- toggle-overlay
  []
  (dom/toggle-class! overlay-view "hide"))

(defn- levelup?
  []
  (not (dom/has-class? overlay-view "hide")))

(defn- clear-selection
  [skill]
  (dom/remove-class! (skill-view skill) "active"))

(defn- clear-skill-selection
  []
  (doall
   (map clear-selection ["strength" "endurance"])))

(defn- refresh-skill-selection
  [entity]
  (when-not (levelup?)
    (entity/set-value entity "levelup-selected-skill" nil)
    (clear-skill-selection)))

(defn- update-entity
  [entity]
  (refresh-skill-selection entity))

(defn- draw-selection
  [skill]
  (dom/add-class! (skill-view skill) "active"))

(defn- draw-selected-skill
  [skill]
  (clear-skill-selection)
  (draw-selection skill))

(defn- next-skill
  [moves entity]
  (let [selected-skill (entity/get-value entity "levelup-selected-skill" nil)
        next-skill (moves selected-skill)]
    (entity/set-value entity "levelup-selected-skill" next-skill)
    (draw-selected-skill next-skill)))

(defn- -skill-up
  [entity]
  (next-skill {nil "endurance"
               "strength" "endurance"
               "endurance" "strength"}
              entity))

(defn- -skill-down
  [entity]
  (next-skill {nil "strength"
               "strength" "endurance"
               "endurance" "strength"}
              entity))

(defn- skill-up
  []
  (when (levelup?)
    (entity/each "player" -skill-up)))

(defn- skill-down
  []
  (when (levelup?)
    (entity/each "player" -skill-down)))

(defn- -upgrade-skill
  [entity]
  (let [selected (entity/get-value entity "levelup-selected-skill")
        selected? (not (nil? selected))
        skill-points (entity/get-value entity "skill-points" 0)
        has-skill-points? (-> skill-points (> 0))]
    (when (-> selected? (and has-skill-points?))

      (let [skill (entity/get-value entity selected)
            new-skill (-> skill (+ 1))
            new-skill-points (-> skill-points (- 1))]
        (entity/set-value entity selected new-skill)
        (entity/set-value entity "skill-points" new-skill-points)))))

(defn- upgrade-skill
  []
  (entity/each "player" -upgrade-skill))

(def bindings {67 toggle-overlay
               38 skill-up
               40 skill-down
               32 upgrade-skill})

(defn keyup
  [key-code]
  (if (contains? bindings key-code)
    ((bindings key-code))))

(defn update
  []
  (entity/each "player" update-entity))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
