(ns selfbot-cljs.commands.RNG.iwant
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn init
  [client]
  (let [sep (.-sep (js/require "path"))
        file (str (.cwd js/process) sep "resources" sep "word-dictionary.json")]
    (set!
     (.-word-dictionary js/exports)
     (js->clj (js/require file) :keywordize-keys true))))

(defn rand-noun
  [dict spice]
  (let [noun-type (if (< (rand-int 100) spice) :specialNouns :nouns)]
    (prn noun-type)
    (rand-nth (noun-type dict))))

(defn run
  "(prefix)iwant [spice]

  Prints the phrase \"I want your ____ in my ____.\" with random words

  `spice` is the percent chance of \"special\" words being used, out of 10. If
  provided, should be between 0 and 10. Default is 2."
  [_ msg [spice]]
  (h/log "Got one command arg" spice (or spice 2))
  (let [spice (.round js/Math (* (or spice 2) 10))
        dict (.-word-dictionary js/exports)]
    ;; `spice` is now an integer from 0 - 100
    (prn "float: " spice)
    (-> (.send (.-channel msg) (str "I want your "
                                    (rand-noun dict spice)
                                    " in my "
                                    (rand-noun dict spice)
                                    "."))
        (.catch h/error))))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "iwant"
              :description "Prints the phrase \"I want your ____ in my ____.\" with random words."
              :usage "[spice:num{0,10}]"
              :usageDelim ""
              :extendedHelp "`spice` is the percent chance of \"special\" words being used, out of 10. If provided, should be between 0 and 10. Default is 2."})

(o/set js/module "exports" #js{:init init
                               :run run
                               :conf conf
                               :help help})
