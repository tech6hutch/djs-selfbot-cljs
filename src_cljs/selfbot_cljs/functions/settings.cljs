(ns selfbot-cljs.functions.settings
  ; (:refer-clojure :rename {get cget, set cset})
  (:require [cljs.reader :as edn]
            [goog.object :as o]
            [selfbot-cljs.core :as h]
            [selfbot-cljs.utils :refer [slurp spit set-timeout]]))

(h/log "Beginning of settings.js (settings.cljs)")

(def ^:private sep (.-sep (js/require "path")))

(defn write-settings
  "Write changes to file"
  [new-settings]
  (h/log "Writing settings to file:" (str new-settings))
  (spit (str (.cwd js/process) sep "storage" sep "settings.edn")
        (str new-settings))
  (str new-settings))

;; Atom
(def settings (try
                (atom (edn/read-string (slurp (str (.cwd js/process) sep
                                                   "storage" sep
                                                   "settings.edn"))))
                (catch :default e
                  (h/warn e)
                  (h/log "Creating file settings.edn")
                  (set-timeout write-settings 2000 {})
                  {}))) ; Set settings to empty map

;; Not atom
; (def settings (try
;                 (edn/read-string (slurp (str (.cwd js/process) sep
;                                              "storage" sep
;                                              "settings.edn")))
;                 (catch :default e
;                   (h/warn e)
;                   (h/log "Creating file settings.edn")
;                   (set-timeout write-settings 2000 {})
;                   {}))) ; Set settings to empty map

; Write changes to file whenever there are changes
; (add-watch settings
;            :write-settings
;            ; 4th arg is new atomic state
;            #(write-settings %4))

(def settings-defaults {:reboot-channel nil
                        :reboot-message nil
                        :reboot-timestamp nil})

(def settings-names (keys settings-defaults))

; (defn get
;   "Gets the value of a setting
;
;   If the given option is not set, returns the default value for that option. If
;   the given option does not exist, throws an error."
;   [option]
;   (if (contains? settings-names option)
;     (if-let [opt (cget @settings option)]
;       opt
;       (cget settings-defaults option))
;     (let [e (str "Option " option " doesn't exist")]
;       (h/error e)
;       (throw (js/Error. e)))))

; (defn set
;   "Sets the value of a setting or settings
;
;   Returns a vector of the old and new value, or false if the setting was
;   unknown."
;   ([options]
;    (if (every? #(contains? settings-names %) options)
;      (let [old-options (map #(cget @settings %) (keys options))]
;        (swap! settings merge options)
;        [old-options options])
;      (do
;        (h/error (js/Error. (str "One of the options doesn't exist")))
;        false)))
;   ([option value]
;    (if (contains? settings-names option)
;      (let [old-value (cget @settings option)]
;        (swap! settings assoc option value)
;        [old-value value])
;      (do
;        (h/error (js/Error. (str "Option " option " doesn't exist")))
;        false))))

; (defn get-all
;   "Gets all settings
;
;   If an option is not set, gives the default value for that option."
;   []
;   (zipmap settings-names (mapv get settings-names)))

(o/set js/module "exports" #js{;:get get
                              ;  :set set
                              ;  :get-all get-all
                               :settings settings
                               :settings-names settings-names
                               :settings-defaults settings-defaults
                               :write-settings write-settings})

(h/log "End of settings.js (settings.cljs)")
