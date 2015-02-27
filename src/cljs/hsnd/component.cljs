(ns hsnd.component)

(def components (atom (vector)))

(defn new
  "Creates new component"
  [entity name hash]
  (let [component {:entity entity
                   :name name
                   :hash (atom hash)}]
    (swap! components conj component)
    component))

(defn by-name
  "Returns all components with specified :name"
  [name]
  (filter #(= (% :name) name)
          @components))

(defn entities
  "Returns entities for provided sequence of components"
  [components]
  (map :entity components))

(defn set
  "Set :key to :value on component"
  [component key value]
  (-> (component :hash) (swap! assoc key value)))

(defn get
  "Get :value of :key on component"
  [component key value]
  (-> @(component :hash) (key)))

(defn reset
  "Resets :hash value"
  [component hash]
  (-> (component :hash) (reset! hash)))

(defn get-hash
  "Get :hash value on component"
  [component]
  @(component :hash))
