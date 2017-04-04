(ns selfbot-cljs.commands.RNG.coinflip
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn run
  "(prefix)coinflip

  Flips a coin"
  [client msg]
  (-> (.send (.-channel msg)
             (str "**" (if (> (js/Math.random) 0.5) "Heads" "Tails") "**"))
      (.catch h/error)))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases ["coin" "flip"]
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []
                    :requiredModules []}))

(def help #js{:name "coinflip"
              :description "Flips a coin."
              :usage ""
              :usageDelim ""})

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
