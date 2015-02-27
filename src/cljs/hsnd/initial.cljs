(ns hsnd.initial
  (:require [hsnd.entity :as entity]))

(def player (entity/new "player"))

(entity/add player "position" {:x 7 :y 15})
(entity/add player "tile" {:value "@"})
(entity/add player "player-controlled" {:dx 0 :dy 0})
