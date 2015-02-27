(ns hsnd.endpoint.resources
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defn resources-endpoint [config]
  (routes
   (route/resources "/")))
