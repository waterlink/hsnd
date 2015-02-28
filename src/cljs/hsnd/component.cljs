(ns hsnd.component)

(def components (atom []))

(defn new
  "Creates new component"
  [entity name hash]
  (let [component {:entity (atom entity)
                   :name name
                   :hash (atom hash)}]
    (swap! components conj component)
    component))

(defn entity
  [component]
  @(component :entity))

(defn by-name
  "Returns all components with specified :name"
  [name]
  (filter #(and (= (% :name) name) (not (nil? (entity %))))
          @components))

(defn entities
  "Returns entities for provided sequence of components"
  [components]
  (map entity components))

(defn set
  "Set :key to :value on component"
  [component key value]
  (-> (component :hash) (swap! assoc key value)))

(defn get
  "Get :value of :key on component"
  [component key]
  (-> @(component :hash) (key)))

(defn reset
  "Resets :hash value"
  [component hash]
  (-> (component :hash) (reset! hash)))

(defn get-hash
  "Get :hash value on component"
  [component]
  @(component :hash))

(defn remove
  [component]
  (-> (component :entity) (reset! nil)))
