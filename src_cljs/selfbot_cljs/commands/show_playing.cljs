(ns selfbot-cljs.commands.show-playing
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn run
  "(prefix)showplaying <@user>

  Shows the user's playing status"
  [client msg [user]]
  (-> (.sendCode (.-channel msg) "" (.. user -presence -game -name))
      (.catch h/error)))

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

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
