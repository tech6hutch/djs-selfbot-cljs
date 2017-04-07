(require 'cljs.build.api)

(cljs.build.api/build "src_cljs"
  {:main 'selfbot-cljs.core
   :output-to "main.js"
   :target :nodejs})
