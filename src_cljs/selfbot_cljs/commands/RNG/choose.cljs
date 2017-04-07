(ns selfbot-cljs.commands.RNG.choose
  (:require [clojure.string :as str]
            [goog.object :as o]
            [selfbot-cljs.core :as h]))

(defn strip-or
  "Removes 'or ' from the last element of coll."
  [coll]
  (conj (subvec coll 0 (dec (count coll)))
        (str/replace (peek coll) #"^or " "")))

(defn parse-choices
  [choices]
  (let [n (count choices)]
    (cond
      ;; `choices` is already split on "|"
      (> n 1) (mapv str/trim choices)
      ;; choices[0] is obviously the only element
      (= n 1) (let [choices (first choices)]
                ;; Try to split on ",", else split on " "
                (if (some #{","} choices)
                  (->> (str/split choices ",") (mapv str/trim) (strip-or))
                  (->> (str/split choices " ") (filterv (complement str/blank?)))))
      :else (assert false "choose got less than one choice"))))

(defn run
  "(prefix)choose <choice1> | <choice2> [| choice3...]
  (prefix)choose <choice1>, <choice2>[, [or] choice3...]
  (prefix)choose <choice1> <choice2> [choice3...]

  Makes a choice for you

  If using the third syntax, choices must be one word."
  [client msg [& choices]]
  (let [choices (parse-choices choices)]
    (-> (.send (.-channel msg) (str "I chose **"
                                    (get choices (rand-int (count choices)))
                                    "**"))
        (.catch h/error))))

(def conf (clj->js {:enabled true
                    :selfbot false
                    :runIn ["text" "dm" "group"]
                    :aliases ["choice" "pick" "decide"]
                    :permLevel 0
                    :botPerms []
                    :requiredFuncs []
                    :requiredModules []}))

(def help #js{:name "choose"
              :description "Makes a choice for you."
              :usage "<choices:str> [...]"
              :usageDelim "|"})

(o/set js/module "exports" #js{:run run
                               :conf conf
                               :help help})
