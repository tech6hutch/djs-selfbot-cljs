(ns selfbot-cljs.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.reader :as edn]
            ; [cljs.js :as cljs])
            [goog.object :as o]
            [selfbot-cljs.utils :refer [slurp]]))

(nodejs/enable-util-print!)

; (def state (cljs/empty-state))

; (defn eval
;   [code cb]
;   (cljs/eval-str state
;                  code
;                  nil
;                  {:eval #(js/eval (:source %))}
;                  #(cb (clj->js %))))

(defn log
  "Calls the JS function console.log with the arguments."
  [& more]
  (apply (.-log js/console) more))

(defn error
  "Calls the JS function console.error with the arguments."
  [& more]
  (apply (.-error js/console) more))

(defn -main [& args]
  (let [config (edn/read-string (slurp (str "config.edn")))
        sep (.-sep (js/require "path"))]
    ;; Komada reads from this environment var
    (o/set (.-env js/process) "clientDir" (str (.cwd js/process) sep
                                               "src_js" sep))
    ;; Initialize Komada
    (.start (js/require "komada")
            #js{:botToken (config :token)
                :ownerID (config :ownerID)
                :clientID (config :ownerID)
                :prefix (config :prefix)
                :selfbot true
                ;; Custom client properties
                :outBaseDir (str (.cwd js/process) sep
                                 "out" sep
                                 "selfbot_cljs" sep)})))

(set! *main-cli-fn* -main)