(ns selfbot-cljs.fs)

(def fs (js/require "fs"))

(defn slurp [f]
  (.readFileSync fs f "utf8"))
