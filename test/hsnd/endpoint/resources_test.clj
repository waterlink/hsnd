(ns hsnd.endpoint.resources-test
  (:require [clojure.test :refer :all]
            [hsnd.endpoint.resources :as resources]))

(def handler
  (resources/resources-endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
