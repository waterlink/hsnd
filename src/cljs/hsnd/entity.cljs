(ns hsnd.entity
  (:require [hsnd.component :as component]))

(defn new
  "Creates new entity"
  [name]
  {:name name
   :components (atom {})})

(defn add
  "Adds a component to entity"
  [entity name hash]
  (let [component (component/new entity name hash)]
    (-> (entity :components) (swap! assoc name component))))

(defn get
  "Fetches component by name"
  [entity name]
  (@(entity :components) name))

(defn get-with-defaults
  "Fetches component by name while creating a default one if it is not there"
  [entity name defaults]
  (if (nil? (get entity name))
    (add entity name defaults))
  (get entity name))

(defn remove
  "Removes a component from entity"
  [entity name]
  (let [component (get entity name)]
    (component/remove component)
    (-> (entity :components) (swap! dissoc name))))

(defn entities-with
  [component-name]
  (component/entities (component/by-name component-name)))

(defn each
  [component-name func]
  (doall (map func (entities-with component-name))))

(defn reduce
  [component-name func value]
  (clojure.core/reduce func value (entities-with component-name)))
