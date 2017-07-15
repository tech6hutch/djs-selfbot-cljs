(ns selfbot-cljs.commands.System.debug
  (:require [clojure.string :refer [lower-case]]
            [goog.object :as o :refer [set] :rename {set oset}]
            [selfbot-cljs.core :refer [error js-async]]
            [selfbot-cljs.utils :refer [slurp]]))

(def fs (js/require "fs"))
(def ^:private sep (.-sep (js/require "path")))

;; Markdown does not support all languages, so map those langs to other langs.
(def rename-langs {"cljs" "clj"})

(defn send-help-message
  [client msg cmd]
  (-> (.-commands client)
      (.get "help")
      (.run client msg [cmd])))

(defn get-code-lang
  [obj type]
  (let [code-lang (or (if (= type "command")
                        (.. obj -help -codeLang)
                        (.-codeLang obj))
                      "JS")]
    (if (string? code-lang)
      code-lang
      (do
        (error "code-lang was not a string:" code-lang)
        (prn code-lang)
        "JS"))))

(defn get-piece-path
  "Returns a vector of dir (including trailing slash) and filename"
  [client type name obj ext]
  ;; Komada base dirs already end with sep
  (let [base-dir (if (= "JS" (get-code-lang obj type))
                   (.-clientBaseDir client)
                   (.-outBaseDir client))
        category (or (o/getValueByKeys obj "help" "category") "General")
        sub-category (or (o/getValueByKeys obj "help" "subCategory") "General")
        cat-dir (str (if (not= category "General") (str sep category))
                     (if (not= sub-category "General") (str sep sub-category)))
        dir (str base-dir type "s" cat-dir)]
    (try
      ;; See if it's a client piece (or overrides a core piece)
      (.accessSync fs (str dir sep name "." ext))
      [(str dir sep) (str name "." ext)]
      (catch :default _
        ;; It must be a core piece
        (let [dir (str (.-coreBaseDir client) type "s" cat-dir)]
          (try
            (.accessSync fs (str dir sep name "." ext))
            [(str dir sep) (str name "." ext)]
            (catch :default _
              ;; Or...maybe not. Can't find it ¯\_(ツ)_/¯
              [nil nil])))))))

(defn send-debug-message
  [client msg type name obj ext]
  (let [code-lang (get-code-lang obj type)
        [dir filename] (get-piece-path client type name obj ext)]
    (.sendCode (.-channel msg)
               "asciidoc"
               (str "code lang :: " code-lang "\n"
                    "location :: " dir "\n"
                    "        src " filename "\n"
                    "     actual " name ".js" "\n"
                    "Type `"
                    (.. msg -guildSettings -prefix) "debug " type " " name " src"
                    "` to see source"))))

(defn send-src-message
  [client chan type name obj ext]
  (if-let [[dir filename] (get-piece-path client type name obj ext)]
    (let [src (slurp (str dir filename))]
      (if (empty? src)
        (.sendCode chan "" "Something went wrong; could not load source")
        (.sendCode chan (get rename-langs ext ext) src #js{:split true})))
    (.sendCode chan "" "Could not find piece")))

(defn run-show-piece-src
  [client msg type name obj ext]
  (-> (send-src-message client (.-channel msg) type name obj ext)
      (.catch error)))

(defn run-show-piece
  [client msg type name obj ext]
  (if (= type "command")
    (-> (send-help-message client msg name)
        (.then #(-> (send-debug-message client msg
                                        type name obj ext)
                    (.catch error)))
        (.catch error))
    (-> (send-debug-message client msg
                            type name obj ext)
        (.catch error))))

(defn run-list-pieces
  [client chan type pieces]
  (let [pieces-names (if (instance? js/Map pieces)
                       (.keyArray pieces)
                       (.keys js/Object pieces))
        pieces-msg (if (> (count pieces-names) 0)
                     (.reduce pieces-names #(str %1 ", " %2))
                     (str "No " type "s loaded"))]
    (-> (.sendCode chan "" pieces-msg)
        (.catch error))))

(defn run
  "(prefix)debug <type> <name> ['src' ext]"
  [client msg [type name src ext]]
  (let [pieces (aget client (case type
                              "command" "commands"
                              "inhibitor" "commandInhibitors"
                              "monitor" "messageMonitors"
                              "function" "funcs"
                              "provider" "providers"
                              "finalizer" "commandFinalizers"))]
    (if (= name "*")
      (run-list-pieces client (.-channel msg) type pieces)
      (let [obj (if (instance? js/Map pieces)
                  (.get pieces name)
                  (aget pieces name))
            code-lang (get-code-lang obj type)
            ext (lower-case (or ext code-lang))]
        (if obj
          (if src
            (run-show-piece-src client msg type name obj ext)
            (run-show-piece client msg type name obj ext))
          (-> (.sendCode (.-channel msg) "" (str "That " type " doesn't exist"))
              (.catch error)))))))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases []
                    :permLevel 10
                    :botPerms []
                    :requiredFuncs []}))

(def help #js{:name "debug"
              :description "Show debugging info on some thing or list all things."
              :usage "<command|inhibitor|monitor|function|provider|finalizer> <name:str> [src] [ext:str]"
              :usageDelim " "
              :extendedHelp "Use `*` as name to list all pieces of that type."})

(oset js/module "exports" #js{:run (js-async run)
                              :conf conf
                              :help help})
