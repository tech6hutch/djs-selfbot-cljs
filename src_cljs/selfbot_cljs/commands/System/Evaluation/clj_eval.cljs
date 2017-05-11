(ns selfbot-cljs.commands.System.Evaluation.clj-eval
  (:require [clojure.string :as str]
            [cljs.js :as cljs]
            [selfbot-cljs.core :refer [log error js-async]]))

; (def state (cljs/empty-state))

; (defn eval-with-vars [client msg code]
;   (prn code)
;   (prn (:source code))
;   (let [guild (.-guild msg)
;         chan (.-channel msg)
;         mentUser (.first msg.mentions.users)
;         mentMem (if mentUser
;                     (.fetchMember guild mentUser))
;
;         user (.-user client)
;         me user
;         pres (.-presence me)
;
;         users (.-users client)
;         guilds (.-guilds client)
;         chans (.-channels client)
;
;         ;; Unqualified symbols are assumed to be in cljs.user by the CLJS eval,
;         ;; so as a hacky work-around we'll just wrap the stuff we want in
;         ;; {cljs: {user: {our variables here}}}. This is then passed to
;         ;; vm.runInNewContext as the global vars that will be accessible in the
;         ;; eval, to the CLJS-compiled JS code.
;         sandbox #js{:cljs #js{:user (.assign js/Object
;                                              #js{:client client
;                                                  :msg msg
;
;                                                  :guild guild
;                                                  :chan chan
;                                                  :mentUser mentUser
;                                                  :mentMem mentMem
;
;                                                  :user user
;                                                  :me me
;                                                  :pres pres
;
;                                                  :users users
;                                                  :guilds guilds
;                                                  :chans chans}
;                                           js/global)}}]
;     (prn (.inspect (js/require "util")
;                    (.-user (.-cljs sandbox))
;                    #js{:depth 0}))
;     (.runInNewContext (js/require "vm") (:source code) sandbox)))
  ; (js/eval (:source code)))

(defn js->clj'
  "Like `cljs.core/js->clj`, but also converts non-plain JS objects.

  `cljs.core/js->clj` returns non-plain JS objects without modification. This
  function will convert those, as well."
  ([x] (js->clj' x :keywordize-keys false))
  ([x & opts]
   (let [c (js->clj x opts)]
     (if (= c x)
       (into {} (for [k (js-keys x)] [k (get x k)]))
       c))))

(defn- js->string [obj]
  (str "(clj->js " (js->clj' obj) ")"))

(defn- build-def-string [[k v]]
  (str "(def " k " " (js->string v) ")"))

(defn build-vars-map
  [client msg]
  (let [guild (.-guild msg)
        mentUser (.first (.. msg -mentions -users))
        user (.-user client)]
    {"guild" guild
     "chan" (.-channel msg)
     "mentUser" mentUser
     "mentMem" (if mentUser (.fetchMember guild mentUser))

     "user" user
     "me" user
     "pres" (.-presence user)

     "users" (.-users client)
     "guilds" (.-guilds client)
     "chans" (.-channels client)}))

(defn- build-vars-string
  "Builds a string that can be concatenated with the code to evaluate to make
  some local vars available to it, for convenience."
  [client msg]
  (->> (map build-def-string (build-vars-map))
       (str/join "\n")))

(defn eval-cb
  "Processes the result (`res`) from the eval and sends it, or the error if
  there is one, to the channel (`chan`)."
  [chan res]
  (if (res :error)
    (-> (.sendMessage chan (str "`ERROR` ```xl\n"
                                (client.funcs.clean client (res :error))
                                "\n```"))
        (.catch error))
    (if (or (string? (res :value)) (coll? (res :value)))
      (-> (.sendCode chan "clj" (client.funcs.clean client (res :value)))
          (.catch error))
      (let [value (.inspect (js/require "util")
                            (res :value)
                            #js{:depth 0})]
        (-> (.sendCode chan "clj" (client.funcs.clean client value))
            (.catch error))))))

(defn run
  [client msg [code]]
  (let [vars-str (build-vars-string client msg)
        chan (.-channel msg)]
    (try
      (cljs/eval-str (cljs/empty-state)
                     (str vars-str "\n" code)
                     nil
                     {:eval cljs/js-eval}
                     (partial eval-cb chan))
      (catch :default e
        (-> (.sendMessage chan (str "`ERROR` ```xl\n"
                                    (client.funcs.clean client e)
                                    "\n```"))
            (.catch error))
        (if (.-stack e)
          (.. client -funcs (log (.-stack e) "error")))))))

(def conf (clj->js {:enabled true
                    :runIn ["text" "dm" "group"]
                    :aliases ["cljs-eval" "clj" "cljs" "ev"]
                    :permLevel 10
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "clj-eval"
              :description "Evaluates arbitrary ClojureScript. Reserved for bot owner."
              :usage "<expression:str>"
              :usageDelim ""})

(aset js/module "exports" #js{:run (js-async run)
                              :conf conf
                              :help help})
