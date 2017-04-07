(ns selfbot-cljs.events.ready
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn run [client]
  (-> (.setStatus (.-user client) "invisible")
      (.catch h/error)))

(o/set js/exports "run" run)
