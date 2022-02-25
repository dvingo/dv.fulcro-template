(ns clj.new.dv.fulcro-template.options.storybook
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+storybook")

;; todo - add task-stories sample namespace.
(defn files [data]
  [[".storybook/.babelrc" (helpers/render ".storybook/.babelrc" data)]
   [".storybook/main.js" (helpers/render ".storybook/main.js" data)]
   [".storybook/manager.js" (helpers/render ".storybook/manager.js" data)]
   [".storybook/preview-head.html" (helpers/render ".storybook/preview-head.html" data)]])
