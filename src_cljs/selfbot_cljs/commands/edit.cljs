(ns selfbot-cljs.commands.edit
  (:require [clojure.string :refer [join]]
            [goog.object :as o]
            [selfbot-cljs.core :as h]
            [selfbot-cljs.utils :refer [set-timeout]]))

(defn run
  "(prefix)edit [n|]<text1>|<text2>

  Edits a message after a delay in milliseconds

  If n (the delay) is not given, defaults to 50 ms."
  [client msg [n text1 text2]]
  (prn "args:" n text1 text2)
  (let [n (.abs js/Math (or n 50))]
    (-> (.edit msg text1)
        (.then (partial set-timeout #(-> (.edit % text2) (.catch h/error)) n))
        (.catch h/error))))

(def conf (clj->js {:enabled true
                    :selfbot true
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "edit"
              :description "Edits a message after a delay in milliseconds."
              :usage "[n:int{0}] <text1:str> <text2:str>"
              :usageDelim "|"})

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
