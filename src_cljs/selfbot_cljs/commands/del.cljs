(ns selfbot-cljs.commands.del
  (:require [clojure.string :refer [join]]
            [selfbot-cljs.core :refer [error js-async]]))

(defn run
  "(prefix)del [n] <text>

  Deletes a message after a delay in milliseconds

  If n (the delay) is not given, defaults to 50 ms."
  [client msg [n & text]]
  (prn "args:" n text)
  (let [n (.abs js/Math (or n 50))
        text (join " " text)]
    (-> (.edit msg text)
        (.then #(-> (.delete % n) (.catch error)))
        (.catch error))))

(def conf (clj->js {:enabled true
                    :selfbot true
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "del"
              :description "Deletes a message after a delay in milliseconds."
              :usage "[n:int{0}] <text:str> [...]"
              :usageDelim " "})

(aset js/module "exports" #js{:run (js-async run)
                              :conf conf
                              :help help})
