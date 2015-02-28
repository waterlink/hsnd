(ns hsnd.callback)

(def callbacks (atom {}))

(defn- listeners-for
  [name]
  (-> @callbacks (name)))

(defn emit
  [name & rest]
  (let [listeners (listeners-for name)]
    (doall (map #(apply % rest) listeners))))

(defn listen
  [name func]
  (if-not (contains? @callbacks name)
    (swap! callbacks assoc name []))
  (swap! callbacks
         assoc name
         (conj (listeners-for name) func)))
