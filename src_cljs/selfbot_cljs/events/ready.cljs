(ns selfbot-cljs.events.ready
  (:require [goog]
            [goog.object :as o]
            [selfbot-cljs.core :as core :refer [log error]]))

(defn run [client]
  ;; Monkeypatch goog.provide so CLJS pieces can be reloaded
  ; (set! goog.provide core/silentProvide))
  ;; I'm literally just removing all its checks
  ; (set! goog.provide (fn [name]
  ;                      (goog.constructNamespace_ name))))
  (log "Ready event"))

(o/set js/exports "run" run)
