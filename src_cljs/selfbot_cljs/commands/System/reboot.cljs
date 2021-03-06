(ns selfbot-cljs.commands.System.reboot
  (:require [selfbot-cljs.core :refer [log error js-async]]))

; (log "Loading reboot.js")
;
; (def default-channel-id "247839977277358081")
;
; (defn- get-reboot-settings [client settings]
;   (if-let [reboot-channel (.get (.-channels client)
;                                 (:reboot-channel @settings))]
;     (if-let [reboot-message (.get (.-messages reboot-channel)
;                                   (:reboot-message @settings))]
;       [reboot-message (:reboot-timestamp @settings)]
;       (do
;         (error "Found reboot channel but could not find message")
;         [nil nil]))
;     (do
;       (log "Starting bot")
;       [nil nil])))
;
; (declare settings)
;
; (defn init
;   [client]
;   (log "In reboot init")
;   (def settings (.. client -funcs -settings))
;   (let [[msg timestamp] (get-reboot-settings client settings)]
;     (if msg
;       (-> (.edit msg (str ":white_check_mark: Successfully rebooted"))
;           (.then #(.edit % (str (.-content %)
;                                 " ("
;                                 (/ (- (.-editedTimestamp %) timestamp)
;                                    1000)
;                                 "s)")))
;           (.catch error))
;       (-> (.get (.-channels client) default-channel-id)
;           (.send "Starting bot")
;           (.catch error)))))

; (defn run
;   "(prefix)reboot
;
;   Reboots the bot"
;   [client msg]
;   (swap! settings assoc :reboot-channel (.. msg -channel -id)
;                         :reboot-message (.-id msg)
;                         :reboot-timestamp (.-createdTimestamp msg))
;   (-> (.send (.-channel msg) ":fuelpump: Rebooting...")
;       (.then #(.exit js/process))
;       (.catch error)))

(defn run
  [client msg [& args]]
  (-> (.send (.-channel msg) "Reboot command")
      (.catch error)))

(def conf (clj->js {:enabled true
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 10
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "reboot"
              :description "Reboots the bot."
              :usage ""})

; (def strings #js{"Reboots the bot." "Redémarre le bot"
;                  "Rebooting..." "Redémarrage"})

(aset js/module "exports" #js{;:init init
                              :run (js-async run)
                              :conf conf
                              :help help})
                              ; :strings strings})
