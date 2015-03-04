(ns hsnd.systems.inventory
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)

(def overlay-view (xpath/xpath "//div[@id='inventory-overlay']"))
(def list-view (xpath/xpath "//div[@id='inventory-list']"))

(defn- item-view-count
  []
  (count (dom/nodes (xpath/xpath list-view "p"))))

(defn- list-item-view
  [index]
  (loop [i (item-view-count)]
    (if (-> i (< index))
      (do
        (dom/append! list-view "<p><p>")
        (recur (inc i)))))
  (xpath/xpath list-view (str "p[" index "]")))

(defn- cleanup-list-view
  [item-count]
  (loop [i (item-view-count)]
    (if (-> i (> item-count))
      (do
        (-> (list-item-view i)
            (dom/set-text! ""))
        (recur (dec i))))))

(defn- inventory-active?
  []
  (not (dom/has-class? overlay-view "hide")))

(defn- toggle-inventory-overlay
  []
  (dom/toggle-class! overlay-view "hide"))

(defn- active-item-component
  []
  (first (component/by-name "inventory-active-item")))

(defn- change-active-item-to
  [old-active-index new-active-index]
  (-> (list-item-view old-active-index)
      (dom/remove-class! "active"))
  (-> (list-item-view new-active-index)
      (dom/add-class! "active")))

(defn- change-active-item
  [change]
  (if (inventory-active?)
    (let [items (entity/each "in-inventory")
          item-count (count items)
          active-component (active-item-component)
          active-item-index (component/get active-component :value)
          new-index (-> active-item-index (- 1) (+ change) (mod item-count) (+ 1))
          null-index 0
          new-index (if (-> item-count (= 0)) null-index new-index)]
      (change-active-item-to active-item-index new-index)
      (component/set active-component :value new-index))))

(defn- inventory-left [] nil)
(defn- inventory-right [] nil)

(defn- inventory-up [] (change-active-item (- 1)))

(defn- inventory-down [] (change-active-item (+ 1)))

(defn- draw-inventory-item
  [index item]
  (let [name (item :name)
        tile (-> (entity/get item "tile") (component/get :value))
        equipped? (not (nil? (entity/get item "equipped")))
        representation (if equipped?
                         (str tile " " name " (equipped)")
                         (str tile " " name))]
    (-> (list-item-view (inc index))
        (dom/set-text! representation))))

(defn- update-player-busy
  [player]
  (let [busy-component (entity/get player "busy")
        busy (inventory-active?)]
    (component/set busy-component :value busy)))

(defn- under-player-feet?
  [player item]
  (let [player-position-component (entity/get player "position")
        player-position? (not (nil? player-position-component))
        item-position-component (entity/get item "position")
        item-position? (not (nil? item-position-component))]
    (if (-> player-position? (and item-position?))
      (let [player-position (component/get-hash player-position-component)
            item-position (component/get-hash item-position-component)]
        (-> player-position (= item-position)))
      false)))

(defn- items-under-player-feet
  [player]
  (filter (partial under-player-feet? player)
          (entity/each "item")))

(defn- pickup-item
  [item]
  (entity/remove item "position")
  (entity/add item "in-inventory" {}))

(defn- pickup-items
  []
  (let [player (first (entity/each "player"))
        items (items-under-player-feet player)]
    (doall (map pickup-item items))))

(defn- equipped-in-slot-for
  [item]
  (let [{slot :slot} (-> (entity/get item "equippable") (component/get-hash))]
    (str "equipped-in-slot:" slot)))

(defn- de-equip
  [item]
  (entity/remove item (equipped-in-slot-for item))
  (entity/remove item "equipped"))

(defn- equip
  [item]
  (let [equipped-in-slot (equipped-in-slot-for item)]
    (entity/each equipped-in-slot de-equip)
    (entity/add item "equipped" {})
    (entity/add item equipped-in-slot)))

(defn- get-active-item
  []
  (let [active-item-index (dec (component/get (active-item-component) :value))
        items (vec (entity/each "in-inventory"))
        active-item? (contains? items active-item-index)]
    (if active-item?
      (items active-item-index)
      nil)))

(defn- equip-active-item
  []
  (let [active-item (get-active-item)]
    (if active-item
      (let [equipped? (not (nil? (entity/get active-item "equipped")))
            equippable? (not (nil? (entity/get active-item "equippable")))
            action (if equipped? de-equip equip)]
        (if equippable?
          (action active-item))))))

(defn- drop-active-item
  []
  (if (inventory-active?)
    (let [active-item (get-active-item)
          player (first (entity/each "player"))
          player-position (-> (entity/get player "position") (component/get-hash))]
      (if active-item
        (do
          (entity/remove active-item "equipped")
          (entity/remove active-item "in-inventory")
          (entity/add active-item "position" player-position))))))

(def bindings {73 toggle-inventory-overlay
               37 inventory-left
               39 inventory-right
               38 inventory-up
               40 inventory-down
               188 pickup-items
               69 equip-active-item
               68 drop-active-item})

(defn keyup
  [key-code]
  (if (contains? bindings key-code)
    ((bindings key-code))))

(defn update
  []
  (entity/each "player" update-player-busy))

(defn draw
  []
  (if (inventory-active?)
    (let [items (entity/each "in-inventory")
          item-count (count items)]
      (cleanup-list-view item-count)
      (doall
       (map-indexed draw-inventory-item items)))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
