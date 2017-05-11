(ns selfbot-cljs.commands.RNG.coinflip
  (:require [selfbot-cljs.core :refer [error js-async]]))

(defn run
  "(prefix)coinflip

  Flips a coin"
  [client msg]
  (-> (.send (.-channel msg)
             (str "**" (if (> (js/Math.random) 0.5) "Heads" "Tails") "**"))
      (.catch error)))

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

(aset js/module "exports" #js{:run (js-async run)
                              :conf conf
                              :help help})
