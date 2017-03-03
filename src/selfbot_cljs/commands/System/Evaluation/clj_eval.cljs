(ns selfbot-cljs.commands.System.Evaluation.clj-eval
  (:require [cljs.js :as cljs]))
  ; (:require [selfbot-cljs.core :as selfbot]))

(def state (cljs/empty-state))

(defn eval-with-vars [client msg code]
  (prn code)
  (prn (:source code))
  (let [guild (.-guild msg)
        chan (.-channel msg)
        mentUser (.first msg.mentions.users)
        mentMem (if mentUser
                    (.fetchMember guild mentUser))

        user (.-user client)
        me user
        pres (.-presence me)

        users (.-users client)
        guilds (.-guilds client)
        chans (.-channels client)

        ;; Unqualified symbols are assumed to be in cljs.user by the CLJS eval,
        ;; so as a hacky work-around we'll just wrap the stuff we want in
        ;; {cljs: {user: {our variables here}}}. This is then passed to
        ;; vm.runInNewContext as the global vars that will be accessible in the
        ;; eval, to the CLJS-compiled JS code.
        sandbox #js{:cljs #js{:user (.assign js/Object
                                             #js{:client client
                                                 :msg msg

                                                 :guild guild
                                                 :chan chan
                                                 :mentUser mentUser
                                                 :mentMem mentMem

                                                 :user user
                                                 :me me
                                                 :pres pres

                                                 :users users
                                                 :guilds guilds
                                                 :chans chans}
                                          js/global)}}]
    (prn (.inspect (js/require "util")
                   (.-user (.-cljs sandbox))
                   #js{:depth 0}))
    (.runInNewContext (js/require "vm") (:source code) sandbox)))
  ; (js/eval (:source code)))

(aset js/exports "run" (fn [client msg [code]]
  (let [chan (.-channel msg)]
    (try
      (cljs/eval-str state code nil {:eval #(eval-with-vars client msg %)} (fn [evaled]
        (if (evaled :error)
          (.catch
           (.sendMessage chan (str "`ERROR` ```xl\n"
                                   (client.funcs.clean client (evaled :error))
                                   "\n```"))
           (.error js/console))
          (if (or (string? (evaled :value)) (coll? (evaled :value)))
            (.catch
             (.sendCode chan "clj" (client.funcs.clean client (evaled :value)))
             (.error js/console))
            (let [value (.inspect (js/require "util")
                                  (evaled :value)
                                  #js{:depth 0})]
              (.catch
               (.sendCode chan "clj" (client.funcs.clean client value))
               (.error js/console)))))))
      (catch :default err
        (.catch
         (.sendMessage chan (str "`ERROR` ```xl\n"
                                 (client.funcs.clean client err)
                                 "\n```"))
         (.error js/console))
        (if (.-stack err)
          (client.funcs.log (.-stack err) "error")))))))

(aset js/exports
      "conf"
      #js{:enabled true
          :guildOnly false
          :aliases #js["cljs-eval" "clj" "cljs" "ev"]
          :permLevel 10
          :botPerms #js[]
          :requiredFuncs #js[]})

(aset js/exports
      "help"
      #js{:name "clj-eval"
          :description "Evaluates arbitrary ClojureScript. Reserved for bot owner."
          :usage "<expression:str>"
          :usageDelim ""})
