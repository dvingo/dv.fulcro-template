(ns clj.new.dv.fulcro-template.options.workspaces
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+workspaces")

(defn files [data]
  [["resources/public/workspaces/workspaces.html" (helpers/render "resources/public/workspaces/workspaces.html" data)]
   ["src/workspaces/{{nested-dirs}}/main_ws.cljs" (helpers/render "src/workspaces/app/main_ws.cljs" data)]])
