(ns clj.new.dv.fulcro-template.options.kondo
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+kondo")

(defn files [data]
  [[".clj-kondo/config.edn" (helpers/render "clj-kondo/config.edn" data)]])
