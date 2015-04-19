(ns hsnd.systems.inventory
  (:require [hsnd.component :as component]
            [hsnd.entity :as entity]
            [hsnd.callback :as callback]
            [domina :as dom]
            [domina.xpath :as xpath]))

(defn init [] nil)
(defn keydown [] nil)
(defn update [] nil)

(defn- by-id [id] (xpath/xpath (str "//div[@id='" id "']")))

(def overlay-view (by-id "inventory-overlay"))
(def list-view (by-id "inventory-list"))

(def details-view (by-id "item-details-overlay"))
(def details-name-view (by-id "item-details-name"))
(def details-description-view (by-id "item-details-description"))
(def stat-list-view (by-id "item-details-stats"))

(defn- view-count
  [view selector]
  (count (dom/nodes (xpath/xpath view selector))))

(defn- item-view-count
  []
  (view-count list-view "p"))

(defn- item-details-stats-view-count
  []
  (view-count stat-list-view "p"))

(defn- generic-list-view
  [current-count view index]
  (loop [i current-count]
    (if (-> i (< index))
      (do
        (dom/append! view "<p><p>")
        (recur (inc i)))))
  (xpath/xpath view (str "p[" index "]")))

(defn- generic-list-cleanup
  [current-count item-view-fn new-count]
  (loop [i current-count]
    (if (-> i (> new-count))
      (do
        (-> (item-view-fn i)
            (dom/set-text! ""))
        (recur (dec i))))))

(defn- list-item-view
  [index]
  (generic-list-view (item-view-count) list-view index))

(defn- list-stat-view
  [index]
  (generic-list-view (item-details-stats-view-count) stat-list-view index))

(defn- cleanup-list-view
  [item-count]
  (generic-list-cleanup (item-view-count) list-item-view item-count))

(defn- cleanup-stats-view
  [item-count]
  (generic-list-cleanup (item-details-stats-view-count) list-stat-view item-count))

(defn- inventory-active?
  []
  (not (dom/has-class? overlay-view "hide")))

(defn- details-active?
  []
  (not (dom/has-class? details-view "hide")))

(defn- toggle-inventory-overlay
  []
  (dom/toggle-class! overlay-view "hide"))

(defn- show-active-item-details
  []
  (dom/remove-class! details-view "hide"))

(defn- back-to-inventory
  []
  (dom/add-class! details-view "hide"))

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

(defn- inventory-left [] (back-to-inventory))

(defn- inventory-right [] (show-active-item-details))

(defn- inventory-up [] (change-active-item (- 1)))

(defn- inventory-down [] (change-active-item (+ 1)))

(defn- get-active-item
  []
  (let [active-item-index (dec (component/get (active-item-component) :value))
        items (vec (entity/each "in-inventory"))
        active-item? (contains? items active-item-index)]
    (if active-item?
      (items active-item-index)
      nil)))

(defn- item-representation
  [item]
  (let [name (item :name)
        tile (-> (entity/get item "tile") (component/get :value))
        equipped? (not (nil? (entity/get item "equipped")))
        representation (if equipped?
                         (str tile " " name " (equipped)")
                         (str tile " " name))]
    representation))

(defn- stat-representation
  [[stat-name stat-effect]]
  (let [effect (if (-> stat-effect (> 0))
                 (str "+" stat-effect)
                 (str stat-effect))]
    (str effect " " (name stat-name))))

(defn- draw-inventory-item
  [index item]
  (-> (list-item-view (inc index))
      (dom/set-text! (item-representation item))))

(defn- draw-details-one-stat
  [index stat]
  (-> (list-stat-view (inc index))
      (dom/set-text! (stat-representation stat))))

(defn- draw-details-stats
  [stats]
  (cleanup-stats-view (count stats))
  (doall
   (map-indexed draw-details-one-stat stats)))

(defn- draw-item-details
  []
  (let [item (get-active-item)
        item? (not (nil? item))]
    (when item?
      (let [name (item-representation item)
            description (-> (entity/get-with-defaults item "description" {:value "No description"})
                            (component/get :value))
            stats (-> (entity/get-with-defaults item "stats" {})
                      (component/get-hash))]
        (dom/set-text! details-name-view name)
        (dom/set-text! details-description-view description)
        (draw-details-stats stats)))))

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
  (entity/add item "in-inventory" {})
  (callback/emit :log-message
                 (str (item-representation item) " picked up")))

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
  (entity/remove item "equipped")
  (callback/emit :log-message
                 (str (item-representation item) " unequipped")))

(defn- equip
  [item]
  (let [equipped-in-slot (equipped-in-slot-for item)]
    (callback/emit :log-message
                   (str (item-representation item) " equipped"))
    (entity/each equipped-in-slot de-equip)
    (entity/add item "equipped" {})
    (entity/add item equipped-in-slot {})))

(defn- equip-active-item
  []
  (if (inventory-active?)
    (let [active-item (get-active-item)]
      (if active-item
        (let [equipped? (not (nil? (entity/get active-item "equipped")))
              equippable? (not (nil? (entity/get active-item "equippable")))
              action (if equipped? de-equip equip)]
          (if equippable?
            (action active-item)))))))

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
          (entity/add active-item "position" player-position)
          (callback/emit :log-message
                         (str (item-representation active-item) " dropped")))))))

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

(defn draw
  []
  (when (inventory-active?)
    (let [items (entity/each "in-inventory")
          item-count (count items)]
      (cleanup-list-view item-count)
      (doall
       (map-indexed draw-inventory-item items)))
    (when (details-active?)
      (draw-item-details))))

(def system {:init init
             :draw draw
             :update update
             :keydown keydown
             :keyup keyup})
