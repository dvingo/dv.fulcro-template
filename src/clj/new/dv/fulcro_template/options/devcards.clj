(ns clj.new.dv.fulcro-template.options.devcards
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+devcards")

(defn files [data]
  [["resources/public/devcards/devcards.html" (helpers/render "resources/public/devcards/devcards.html" data)]
   ["src/devcards/{{nested-dirs}}/devcards.cljs" (helpers/render "src/devcards/app/devcards.cljs" data)]])
