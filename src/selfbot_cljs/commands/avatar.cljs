(ns selfbot-cljs.commands.avatar
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn run
  "(prefix)avatar <@user1> [@user2 ...]

  Posts the avatar of each user"
  [client msg [& users]]
  ;; The .map method of Collections (Maps) and Arrays works the same. ¯\_(ツ)_/¯
  (let [users (if (empty? users) #js[(.-user client)] users)
        chan (.-channel msg)]
    (.map users #(-> chan
                     (.send (str "<@" (.-id %) ">'s avatar:\n" (.-avatarURL %)))
                     (.catch h/error)))))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases ["icon" "a" "ava"]
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "avatar"
              :description "Posts the avatar of each user."
              :usage "[users:user] [...]"
              :usageDelim " "})

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
