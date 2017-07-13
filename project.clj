(defproject selfbot-cljs "0.2.0-SNAPSHOT"
  :description "My selfbot."
  :url "https://github.com/tech6hutch/djs-selfbot-cljs#readme"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.562"]
                 [org.clojure/core.async "0.3.443"]
                 [jamesmacaulay/cljs-promises "0.1.0"]]
  :plugins [[lein-cljsbuild "1.1.5"]]
  :cljsbuild {:builds {:main {:source-paths ["src_cljs"]
                              :compiler {:main selfbot-cljs.core
                                         :output-dir "out"
                                         :output-to "main.js"
                                         :optimizations :none
                                         :target :nodejs}}}})
