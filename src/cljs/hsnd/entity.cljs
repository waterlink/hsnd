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

(defn remove
  "Removes a component from entity"
  [entity name]
  (-> (entity :components) (swap! dissoc name)))

(defn get
  "Fetches component by name"
  [entity name]
  (@(entity :components) name))
