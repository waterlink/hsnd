(defproject hsnd "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :source-paths ["src/clj", "src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.stuartsierra/component "0.2.2"]
                 [compojure "1.3.1"]
                 [duct "0.1.0"]
                 [environ "1.0.0"]
                 [meta-merge "0.1.1"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring-jetty-component "0.2.2"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [domina "1.0.3"]]

  :plugins [[lein-environ "1.0.0"]
            [lein-gen "0.2.2"]
            [lein-cljsbuild "1.0.5"]]

  :cljsbuild {:builds
              [{:source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/hsnd.js"
                           :optimizations :whitespace
                           :pretty-print true}}]}

  :generators [[duct/generators "0.1.0"]]
  :duct {:ns-prefix hsnd}
  :main ^:skip-aot hsnd.main

  :aliases {"gen"   ["generate"]
            "setup" ["do" ["generate" "locals"]]}

  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :dependencies [[reloaded.repl "0.1.0"]
                                  [org.clojure/tools.namespace "0.2.8"]
                                  [kerodon "0.5.0"]]
                   :env {:port 3000}}
   :project/test  {}})
