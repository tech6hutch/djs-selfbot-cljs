(ns selfbot-cljs.commands.raw
  (:require [selfbot-cljs.core :refer [error js-async]]))

(defn run
  "(prefix)raw <text>

  Escapes the Markdown in the text"
  [client msg [text]]
  (-> (.edit msg
             (-> client .-methods (.escapeMarkdown text)))
      (.catch error)))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "raw"
              :description "Escapes the Markdown in the text."
              :usage "<text:str>"})

(aset js/module "exports" #js{:run (js-async run)
                              :conf conf
                              :help help})
