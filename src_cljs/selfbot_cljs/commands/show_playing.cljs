(ns selfbot-cljs.commands.show-playing
  (:require [selfbot-cljs.core :refer [error js-async]]))

(defn run
  "(prefix)showplaying <@user>

  Shows the user's playing status"
  [client msg [user]]
  (-> (.sendCode (.-channel msg) "" (.. user -presence -game -name))
      (.catch error)))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases ["show-playing" "show_playing"]
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "showplaying"
              :description "Shows the user's playing status."
              :usage "<user:user>"})

(aset js/module "exports" #js{:run (js-async run)
                              :conf conf
                              :help help})
