(ns selfbot-cljs.commands.raw
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn run
  "(prefix)raw <text>

  Escapes the Markdown in the text"
  [client msg [text]]
  (.edit msg
         (-> client .-methods (.escapeMarkdown text))))

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

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
