(ns hsnd.registry
  (:require [hsnd.entity :as entity]))

(defn goblin[name]
  (let [x (entity/new name)]
    (entity/add x "has-stats" {})
    (entity/add x "tile" {:value "g"})
    (entity/add x "enemy" {})
    (entity/add x "gives-xp" {:value 2})
    (entity/add x "level" {:value 1})
    (entity/add x "strength" {:value 3})
    (entity/add x "endurance" {:value 0})
    (entity/add x "sight" {:radius 10})
    (entity/add x "melee-ai" {})
    (entity/add x "loot" {:value
                               [["goblin body"
                                 [["tile" {:value "~"}]
                                  ["passable" {}]]]
                                ["knife"
                                 [["tile" {:value "|"}]
                                  ["stats" {:damage 5}]
                                  ["item" {}]
                                  ["equippable" {:slot "weapon"}]
                                  ["cost" {:value 70}]
                                  ["passable" {}]]]]})
    x))

(defn goblin-cheftain[name]
  (let [x (goblin name)]
    (entity/add x "gives-xp" {:value 5})
    (entity/add x "level" {:value 3})
    (entity/add x "strength" {:value 5})
    (entity/add x "endurance" {:value 1})
    (entity/add x "loot" {:value
                               [["goblin body"
                                 [["tile" {:value "~"}]
                                  ["passable" {}]]]
                                ["stone of regen"
                                 [["tile" {:value "="}]
                                  ["stats" {:regen 4}]
                                  ["item" {}]
                                  ["equippable" {:slot "accessory"}]
                                  ["cost" {:value 3000}]
                                  ["passable" {}]]]]})
    x))
