(ns hsnd.systems.busy
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn keyup [] nil)
(defn draw [] nil)

(def overlays ["inventory-overlay" "level-up-overlay"])

(defn- overlay-query [name] (str "//div[@id='" name "']"))
(defn- overlay-view-raw [name] (xpath/xpath (overlay-query name)))
(def overlay-view (memoize overlay-view-raw))

(defn- overlay-visible? [name] (not (dom/has-class? (overlay-view name) "hide")))

(def any? (comp boolean some))

(defn- busy?
  []
  (->> overlays
       (map overlay-visible?)
       (any? true?)))

(defn- update-busy
  [entity]
  (entity/set-value entity "busy" (busy?)))

(defn update
  []
  (entity/each "player" update-busy))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
