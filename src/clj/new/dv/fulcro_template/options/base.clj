(ns clj.new.dv.fulcro-template.options.base
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(defn files [data]
  (concat
    [;; Top level files
     [".gitignore" (helpers/render "gitignore" data)]
     ["deps.edn" (helpers/render "deps.edn" data)]
     ["guardrails.edn" (helpers/render "guardrails.edn" data)]
     ["package.json" (helpers/render "package.json" data)]
     ["Makefile" (helpers/render "Makefile" data)]
     ["README.md" (helpers/render "README.md" data)]
     ["shadow-cljs.edn" (helpers/render "shadow-cljs.edn" data)]

     ;; Scripts
     ["scripts/build_prod.sh" (helpers/render "scripts/build_prod.sh" data)]
     ["scripts/start_dev.sh" (helpers/render "scripts/start_dev.sh" data)]

     ;; Client
     ["src/main/{{nested-dirs}}/client/application.cljs" (helpers/render "src/main/app/client/application.cljs" data)]
     ["src/main/{{nested-dirs}}/client/client_entry.cljs" (helpers/render "src/main/app/client/client_entry.cljs" data)]
     ["src/main/{{nested-dirs}}/client/prn_debug.cljs" (helpers/render "src/main/app/client/prn_debug.cljs" data)]
     ["src/main/{{nested-dirs}}/client/development_preload.cljs" (helpers/render "src/main/app/client/development_preload.cljs" data)]
     ["src/main/{{nested-dirs}}/client/router.cljs" (helpers/render "src/main/app/client/router.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui/task_item.cljs" (helpers/render "src/main/app/client/ui/task_item.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui/task_page.cljs" (helpers/render "src/main/app/client/ui/task_page.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui/root.cljs" (helpers/render "src/main/app/client/ui/root.cljs" data)]
     ["src/main/config/fe-config.edn" (helpers/render "src/main/config/fe-config.edn" data)]

     ;; Data model
     ["src/main/{{nested-dirs}}/data_model/task.cljc" (helpers/render "src/main/app/data_model/task.cljc" data)]]

    ;; index.html used by client only shadow-cljs devtools
    (when-not (:server? data)
      [["resources/public/index.html" (helpers/render "resources/public/index.html" data)]
       ["resources/public/favicon.ico" (helpers/render "resources/public/favicon.ico" data)]])))

