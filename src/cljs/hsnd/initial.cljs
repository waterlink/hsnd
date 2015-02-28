(ns hsnd.initial
  (:require [hsnd.entity :as entity]))

(def player (entity/new "player"))
(entity/add player "position" {:x 7 :y 15})
(entity/add player "tile" {:value "@"})
(entity/add player "player-controlled" {:dx 0 :dy 0})
(entity/add player "camera" {:active true})
(entity/add player "damage" {:value 1})

(def rock (entity/new "rock"))
(entity/add rock "position" {:x 5 :y 5})
(entity/add rock "tile" {:value "#"})

(def example-log (entity/new "welcome-message"))
(entity/add example-log "log-message" {:value "Welcome to Hack, Slash and Deathblaze!"})

(def goblin (entity/new "goblin"))
(entity/add goblin "position" {:x 15 :y 5})
(entity/add goblin "tile" {:value "g"})
(entity/add goblin "enemy" {})
(entity/add goblin "health" {:value 7})
(entity/add goblin "loot" {:value
                           [["goblin body"
                             [["tile" {:value "~"}]
                              ["passable" {}]]]
                            ["knife"
                             [["tile" {:value "|"}]
                              ["cost" {:value 32}]
                              ["passable" {}]]]]})
