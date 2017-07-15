(ns selfbot-cljs.events.messageDelete
  (:require [goog.object :refer [set] :rename {set oset}]
            [selfbot-cljs.core :as h]))

(def del-watch-guilds #{"218618899158007808"   ; Neon's TFH server
                        "221910104495095808"   ; KekBot server
                        "287724189283254273"   ; KekBot gaming lounge
                        "298429737527672832"   ; Godson's stream server
                        "260202843686830080"}) ; York's server

(def del-log-channel "267110067298500608")

(defn run [client msg]
  (when (contains? del-watch-guilds (.-id (.-guild msg)))
    (let [guild (.-guild msg)
          chan (.-channel msg)
          user (.-author msg)
          mem (.-member msg)]
      (-> (.get (.-channels client) del-log-channel)
          (.send (str "On server "
                      (.-name guild)
                      " in channel "
                      (.-name chan)
                      ", `"
                      (.-username user)
                      "#"
                      (.-discriminator user)
                      "`"
                      (if (.-nickname mem) (str "(\"" (.-nickname mem) "\")"))
                      "'s message was deleted:\n"
                      (.-content msg)))
          (.catch h/error)))))

(oset js/exports "run" run)
