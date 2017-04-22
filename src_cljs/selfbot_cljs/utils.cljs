(ns selfbot-cljs.utils
  (:refer-clojure :exclude [delay]))
  ;; For the linter 😐
  ; (:require js))

;; File system

(def fs (js/require "fs"))

(defn slurp [f]
  (.readFileSync fs f "utf8"))

(defn spit [f content]
  (.writeFileSync fs f content))

;; JS helpers

(def timer (atom js/global))

(defn set-timeout
  "Wrapper for timer.setTimeout"
  ([f] (.setTimeout @timer f))
  ([f delay] (.setTimeout @timer f delay))
  ([f delay & params] (apply (.-setTimeout @timer) f delay params)))

(defn delay
  "Turns set-timeout into a Promise"
  [delay]
  ;; % is the Promise's resolve function
  (js/Promise. #(set-timeout % delay)))
