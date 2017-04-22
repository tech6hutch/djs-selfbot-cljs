(ns selfbot-cljs.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.reader :as edn]
            ; [cljs.js :as cljs])
            [goog.object :as o]
            [selfbot-cljs.utils :as utils :refer [slurp]]))

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

(defn warn
  "Calls the JS function console.warn with the arguments."
  [& more]
  (apply (.-warn js/console) more))

(defn error
  "Calls the JS function console.error with the arguments."
  [& more]
  (apply (.-error js/console) more))

(defn -main [& args]
  (let [config (edn/read-string (slurp (str "config.edn")))
        sep (.-sep (js/require "path"))]
    ;; Komada reads from these environment vars
    (set! (.-env js/process)
          (.assign js/Object
                   (.-env js/process)
                   #js{:clientDir (str (.cwd js/process) sep
                                       "src_js")
                       :outDir (str (.cwd js/process) sep
                                    "out" sep
                                    "selfbot_cljs")
                       :compiledLang "cljs"}))
    ;; Initialize Komada
    (->> (.start (js/require "komada")
                 #js{:botToken (config :token)
                     :ownerID (config :ownerID)
                     :clientID (config :ownerID)
                     :prefix (config :prefix)
                     :selfbot true})
        ;; Set the timer object (for set-timeout) (timers on Discord.Client
        ;; objects are automatically cancelled if the client is destroyed).
        ;; For code in commands, inhibitors, monitors, providers, functions, and
        ;; events that's outside an exported function or is in an init, this
        ;; call to reset! will not have happened yet.
        (reset! utils/timer))))

(set! *main-cli-fn* -main)
