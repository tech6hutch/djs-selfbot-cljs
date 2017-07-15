(ns selfbot-cljs.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.reader :as edn]
            ; [cljs.js :as cljs])
            [goog]
            [selfbot-cljs.utils :as utils :refer [slurp]]
            [cljs-promises.async]))

(nodejs/enable-util-print!)

(cljs-promises.async/extend-promises-as-pair-channels!)

; (def state (cljs/empty-state))

; (defn eval
;   [code cb]
;   (cljs/eval-str state
;                  code
;                  nil
;                  {:eval #(js/eval (:source %))}
;                  #(cb (clj->js %))))

(defn log
  "Logs to console."
  [& more]
  (apply (.-log js/console) more))

(defn warn
  "Logs the warning to console."
  [& more]
  (apply (.-warn js/console) more))

(defn error
  "Logs the error to console."
  [& more]
  (apply (.-error js/console) more))

(defn js-async
  "Turns f into the equivalent of an ES8 async function."
  [f]
  (fn [& more] (.resolve js/Promise (apply f more))))

(defn- -main [& args]
  ;; Monkeypatch goog.provide so CLJS pieces can be reloaded
  ;; (I'm literally just removing all its checks)
  (set! goog.provide (fn [name]
                       (goog.constructNamespace_ name)))
  (let [Komada (js/require "komada")
        config (edn/read-string (slurp (str "config.edn")))
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
    (let [client (Komada.Client. #js{:ownerID (config :ownerID)
                                     :prefix (config :prefix)
                                     :selfbot true
                                     :cmdPrompt true})]
      (.login client (config :token)))))

(set! *main-cli-fn* -main)
