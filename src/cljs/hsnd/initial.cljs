(ns hsnd.initial
  (:require [hsnd.entity :as entity]))

(def player (entity/new "player"))
(entity/add player "player" {})
(entity/add player "position" {:x 7 :y 15})
(entity/add player "tile" {:value "@"})
(entity/add player "player-controlled" {:dx 0 :dy 0})
(entity/add player "camera" {:active true})
(entity/add player "damage" {:value 1})
(entity/add player "health" {:value 100})
(entity/add player "max-health" {:value 100})

(def rock (entity/new "rock"))
(entity/add rock "position" {:x 5 :y 5})
(entity/add rock "tile" {:value "#"})

(def resurrection-stone (entity/new "resurrection stone"))
(entity/add resurrection-stone "position" {:x 25 :y 25})
(entity/add resurrection-stone "tile" {:value "®"})
(entity/add resurrection-stone "resurrection-stone" {:radius 5})
(entity/add resurrection-stone "resurrection-stone-active" {})

(def resurrection-stone-2 (entity/new "resurrection stone"))
(entity/add resurrection-stone-2 "position" {:x -5 :y -5})
(entity/add resurrection-stone-2 "tile" {:value "®"})
(entity/add resurrection-stone-2 "resurrection-stone" {:radius 10})

(def example-log (entity/new "welcome-message"))
(entity/add example-log "log-message" {:value "Welcome to Hack, Slash and Deathblaze!"})

(def goblin (entity/new "goblin"))
(entity/add goblin "position" {:x 15 :y 5})
(entity/add goblin "tile" {:value "g"})
(entity/add goblin "enemy" {})
(entity/add goblin "damage" {:value 1})
(entity/add goblin "sight" {:radius 10})
(entity/add goblin "melee-ai" {})
(entity/add goblin "health" {:value 7})
(entity/add goblin "loot" {:value
                           [["goblin body"
                             [["tile" {:value "~"}]
                              ["passable" {}]]]
                            ["knife"
                             [["tile" {:value "|"}]
                              ["cost" {:value 32}]
                              ["passable" {}]]]]})
