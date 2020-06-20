(ns clj.new.dv.fulcro-template.options.cider
  (:require [clj.new.dv.fulcro--template.options.helpers :as helpers]))

(def option "+cider")

(defn files [data]
  [[".dir-locals.el" (helpers/render "dir-locals.el" data)]])
