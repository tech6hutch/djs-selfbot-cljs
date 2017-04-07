(ns selfbot-cljs.utils)

;; File system

(def fs (js/require "fs"))

(defn slurp [f]
  (.readFileSync fs f "utf8"))

;; JS helpers

(defn set-timeout
  "Wrapper for global.setTimeout"
  ([f] (.setTimeout js/global f))
  ([f delay] (.setTimeout js/global f delay))
  ([f delay & params] (apply (.-setTimeout js/global) f delay params)))
