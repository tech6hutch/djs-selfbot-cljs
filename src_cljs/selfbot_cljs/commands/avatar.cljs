(ns selfbot-cljs.commands.avatar
  (:require [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn send-avatar
  [chan user]
  (-> chan
      (.send (str "<@" (.-id user) ">'s avatar:\n" (.-avatarURL user)))
      (.catch h/error)))

(defn run
  "(prefix)avatar [@user1] [@user2 ...]

  Posts the avatar of each user, or your own"
  [client msg [& users]]
  (let [users (if (empty? users) [(.-user client)] users)]
    (dorun (map (partial send-avatar (.-channel msg)) users))))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases ["icon" "a" "ava"]
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "avatar"
              :description "Posts the avatar of each user, or your own."
              :usage "[users:user] [...]"
              :usageDelim " "})

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
