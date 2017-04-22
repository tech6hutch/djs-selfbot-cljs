(ns selfbot-cljs.commands.System.debug
  (:require [clojure.string :refer [lower-case]]
            [selfbot-cljs.core :as h]
            [selfbot-cljs.utils :refer [slurp]]))

(def ^:private sep (.-sep (js/require "path")))

;; Markdown does not support all languages, so map those langs to other langs.
(def rename-langs {"cljs" "clj"})

(defn send-help-message
  [client msg cmd]
  (-> (.-commands client)
      (.get "help")
      (.run client msg [cmd])))

(defn get-piece-path
  "Returns a vector of dir (including trailing slash) and filename"
  [client type name obj ext]
  ;; Komada base dirs already end with sep
  (let [base-dir (if (= "JS"
                        (if (= type "command")
                          (.. obj -help -codeLang)
                          (.-codeLang obj)))
                   (.-clientBaseDir client)
                   (.-outBaseDir client))
        category (.. obj -help -category)
        sub-category (.. obj -help -subCategory)
        cat-dir (str (if (not= category "General") (str sep category))
                     (if (not= sub-category "General") (str sep sub-category)))
        dir (str base-dir type "s" cat-dir)]
    (try
      ;; See if it's a client piece (or overrides a core piece)
      (.accessSync (js/require "fs") (str dir sep name "." ext))
      [(str dir sep) (str name "." ext)]
      (catch :default _
        ;; It must be a core piece
        (let [dir (str (.-coreBaseDir client) type "s" cat-dir)]
          (try
            (.accessSync (js/require "fs") (str dir sep name "." ext))
            [(str dir sep) (str name "." ext)]
            (catch :default _
              ;; Or...maybe not. Can't find it ¯\_(ツ)_/¯
              [nil nil])))))))


(defn send-debug-message
  [client chan type name obj ext]
  (let [cmd? (= type "command")
        code-lang (or (if cmd? (.. obj -help -codeLang) (.-codeLang obj))
                      "JS")
        [dir filename] (get-piece-path client type name obj ext)]
    (.sendCode chan "asciidoc" (str "code lang :: " code-lang "\n"
                                    "location :: "
                                    dir "\n"
                                    "        src " filename "\n"
                                    "     actual " name ".js" "\n"))))

(defn send-src-message
  [client chan type name obj ext]
  (if-let [[dir filename] (get-piece-path client type name obj ext)]
    (let [src (slurp (str dir filename))]
      (if (empty? src)
        (.sendCode chan "" "Something went wrong; could not load source")
        (.sendCode chan (get rename-langs ext ext) src #js{:split true})))
    (.sendCode chan "" "Could not find piece")))

(defn run
  "(prefix)debug <type> <name> ['src' ext]"
  [client msg [type name src ext]]
  (let [obj (-> (case type
                   "command" (.-commands client)
                   "inhibitor" (.-commandInhibitors client)
                   "monitor" (.-messageMonitors client)
                   "function" (.-funcs client)
                   "provider" (.-providers client))
                (.get name))
        code-lang (or (if (= type "command")
                        (.. obj -help -codeLang)
                        (.-codeLang obj))
                      "JS")
        ext (lower-case (or ext code-lang))]
    (if src
      (-> (send-src-message client (.-channel msg) type name obj ext)
          (.catch h/error))
      (if (= type "command")
        (-> (send-help-message client msg name)
            (.then #(-> (send-debug-message client (.-channel msg)
                                            type name obj ext)
                        (.catch h/error)))
            (.catch h/error))
        (-> (send-debug-message client (.-channel msg)
                                type name obj ext)
            (.catch h/error))))))


(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "debug"
              :description "Show debugging info on some thing."
              :usage "<command|inhibitor|monitor|function|provider> <name:str> [src] [ext:str]"
              :usageDelim " "
              :extendedHelp ""})

(aset js/module "exports" #js{:run run
                              :conf conf
                              :help help})
