(ns hsnd.initial
  (:require [hsnd.entity :as entity]
            [hsnd.registry :as registry]))

(def player (entity/new "player"))
(entity/add player "has-stats" {})
(entity/add player "player" {})
(entity/add player "position" {:x 7 :y 15})
(entity/add player "tile" {:value "@"})
(entity/add player "player-controlled" {:dx 0 :dy 0})
(entity/add player "camera" {:active true})
(entity/add player "busy" {:value false})

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

(def goblin-1 (registry/goblin "goblin"))
(entity/add goblin-1 "position" {:x 15 :y 5})

(def goblin-2 (registry/goblin "goblin"))
(entity/add goblin-2 "position" {:x 20 :y 3})

(def goblin-3 (registry/goblin "goblin"))
(entity/add goblin-3 "position" {:x 13 :y 7})

(def goblin-4 (registry/goblin "goblin"))
(entity/add goblin-4 "position" {:x 12 :y 4})

(def cheftain (registry/goblin-cheftain "goblin cheftain"))
(entity/add cheftain "position" {:x 17 :y 10})

(def small-knife (entity/new "small knife"))
(entity/add small-knife "in-inventory" {})
(entity/add small-knife "stats" {:damage 2})
(entity/add small-knife "equippable" {:slot "weapon"})
(entity/add small-knife "item" {})
(entity/add small-knife "passable" {})
(entity/add small-knife "tile" {:value "|"})

(def leather-jacket (entity/new "leather jacket"))
(entity/add leather-jacket "in-inventory" {})
(entity/add leather-jacket "stats" {:armor 3 :health 20})
(entity/add leather-jacket "equippable" {:slot "chest"})
(entity/add leather-jacket "item" {})
(entity/add leather-jacket "passable" {})
(entity/add leather-jacket "tile" {:value "["})

(def junk (entity/new "some junk"))
(entity/add junk "in-inventory" {})
(entity/add junk "description" {:value "Useless junk"})
(entity/add junk "item" {})
(entity/add junk "passable" {})
(entity/add junk "tile" {:value "%"})

(def active-inventory-item (entity/new "active inventory item"))
(entity/add active-inventory-item "inventory-active-item" {:value 0})
