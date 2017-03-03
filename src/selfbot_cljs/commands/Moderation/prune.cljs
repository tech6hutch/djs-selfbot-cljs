(ns selfbot-cljs.commands.Moderation.prune
  (:require [selfbot-cljs.core :as h]))

(aset js/exports "run" (fn
  "(prefix)prune <n>

  Deletes `n` of my messages in this channel

  <n> is the number of messages to delete (it only looks in the last 100
  messages in the channel, though, including other people's messages, so it
  may not find `n` messages of my own)."
  [client msg [n]]
  (-> (.-channel msg)
      (.fetchMessages #js{:limit 100})
      (.then (fn [messages]
        (let [n (if (-> client .-config .-selfbot) (inc n) n)
              my-messages (-> messages
                              (.filter #(= (-> % .-author .-id)
                                           (-> client .-user .-id)))
                              .array
                              (.slice 0 n))]
          (-> js/Promise
              (.all (.map my-messages #(-> % .delete (.catch h/error))))
              (.then (fn [deleted-msgs]
                (h/log (if (= (.-length deleted-msgs) 1)
                  (str "Deleted " (.-length deleted-msgs) " message")
                  (str "Deleted " (.-length deleted-msgs) " messages")))))
              (.catch h/error)))))
      (.catch h/error))))

(aset js/exports "conf" #js{:enabled true
                            :selfbot true
                            :runIn #js["text" "dm" "group"]
                            :aliases #js["purge"]
                            :permLevel 0
                            :botPerms #js[]
                            :requiredFuncs #js[]
                            :requiredModules #js[]})

(aset js/exports "help" #js{:name "prune"
                            :description "Deletes <n> of my messages in this channel."
                            :usage "<n:int{1,100}>"
                            :usageDelim ""
                            :extendedHelp "<n> is the number of messages to delete (it only looks in the last 100 messages in the channel, though, including other people's messages, so it may not find `n` messages of my own)."})
