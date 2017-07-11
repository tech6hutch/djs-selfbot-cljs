(ns selfbot-cljs.commands.embed
  (:require [selfbot-cljs.core :refer [error]]))

(defn run
  "(prefix)embed <text>

  Embeds some text"
  [client msg [text]]
  (.delete msg)
  (let [embed (-> (new (.. client -methods -Embed))
                  (.setDescription text)
                  (.setColor #js [114 137 218]))]
    (-> (.-channel msg)
        (.send #js {:embed embed})
        (.catch error))))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases [">"]
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "embed"
              :description "Embeds some text."
              :usage "<text:str>"})

(aset js/module "exports" #js{:run run
                              :conf conf
                              :help help})
