(ns hsnd.story-mode
  (:require [hsnd.entity :as entity]
            [hsnd.registry :as registry]))

(defn player[]
  (let [x (first (entity/entities-with "player"))]
    (if (nil? x)
      (registry/player "player")
      x)))

(defn- layout-char[[x y] signs index character]
  (let [x (+ x index)
        factory (get signs character)]
    (when-not (nil? factory)
      (let [e (factory)]
        (entity/add e "position" {:x x :y y})))))

(defn- layout-line[[x y] signs index line]
  (doall
    (map-indexed (partial layout-char [x (+ y index)] signs) line)))

(defn layout[[x y] strings signs]
  (doall
    (map-indexed (partial layout-line [x y] signs) strings)))
