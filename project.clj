(defproject oneword "0.1.0-SNAPSHOT"
  :description "Trivial app for creative writing"
  :url "http://github.com/tel/cljs-oneword"
  :license {:name "BSD 3-clause"
            :url  "http://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [oak "0.1.3-SNAPSHOT"]
                 [rococo "0.1.0-SNAPSHOT"]
                 [prismatic/schema "1.1.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [garden "1.3.2"]
                 [bidi "2.0.6"]
                 [mount "0.1.10"]]
  :plugins [[lein-figwheel "0.5.1"]
            [lein-cljsbuild "1.1.3"]]

  :clean-targets ^{:protect false} ["resources/public/js" "target"]
  :checkout-deps-shares [:source-paths :resource-paths :compile-path]

  :cljsbuild
  {:builds [{:id "dev"
             :source-paths ["src" "checkouts/oak/src" "checkouts/rococo/src"]
             :figwheel true
             :compiler {:main oneword.core
                        :asset-path "js/out"
                        :output-to "resources/public/js/app.js"
                        :output-dir "resources/public/js/out"
                        :source-map-timestamp true}}]})
