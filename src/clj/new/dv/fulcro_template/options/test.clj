(ns clj.new.dv.fulcro-template.options.test
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+test")

(defn files [data]
  [["src/test/{{nested-dirs}}/app_tests.cljc" (helpers/render "src/test/app/app_tests.cljc" data)]
   ["karma.conf.js" (helpers/render "karma.conf.js" data)]])
