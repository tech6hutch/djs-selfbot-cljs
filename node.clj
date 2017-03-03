(require 'cljs.build.api)

(cljs.build.api/build "src"
  {:main 'selfbot-cljs.core
   :output-to "main.js"
   :target :nodejs})
