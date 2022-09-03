(ns clj.new.dv.fulcro-template.options.node-server
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+node-server")

(defn files [data]
  [["src/main/{{nested-dirs}}/node_server.cljs" (helpers/render "src/main/app/node_server.cljs" data)]])
