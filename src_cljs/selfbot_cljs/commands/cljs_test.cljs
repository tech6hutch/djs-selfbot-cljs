(ns selfbot-cljs.commands.cljs-test)

(aset js/exports "run" (fn [client msg & args]
                        (.sendMessage (.-channel msg) "Test successful!")))

(aset js/exports "conf" #js{:enabled true
                            :runIn #js["text" "dm" "group"]
                            :aliases #js[]
                            :permLevel 0
                            :botPerms #js[]
                            :requiredFuncs #js[]})

(aset js/exports "help" #js{:name "cljs-test"
                            :description "Testing."
                            :usage ""
                            :usageDelim ""})
