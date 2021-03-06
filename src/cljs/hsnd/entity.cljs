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

(defn get-value
  "Fetches value out of single-value component"
  ([entity name] (-> (get entity name) (component/get :value)))
  ([entity name default] (-> (get-with-defaults entity name {:value default}) (component/get :value))))

(defn set-value
  "Sets value of single-value component"
  [entity name value]
  (-> (get-with-defaults entity name {:value value}) (component/set :value value)))

(defn remove
  "Removes a component from entity"
  [entity name]
  (let [component (get entity name)]
    (if (not (nil? component))
      (do
        (component/remove component)
        (-> (entity :components) (swap! dissoc name))))))

(defn entities-with
  [component-name]
  (component/entities (component/by-name component-name)))

(defn each
  ([component-name] (each component-name identity))
  ([component-name func]
   (doall (map func (entities-with component-name)))))

(defn reduce
  [component-name func value]
  (clojure.core/reduce func value (entities-with component-name)))
